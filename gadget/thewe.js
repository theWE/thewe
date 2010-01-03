if (typeof console == 'undefined') {
        console = {
                log: function() {
                }
        };
}

shouldntHappen = function() {
    console.log("Shouldn't happen!");
};

$extend(JSON, {stringify: JSON.encode, parse: JSON.decode});

we = {};

$not = function(f) {
        return function(x) {
                return !f(x);
        };
};

String.implement({
        beginsWith: function(pre) {
                return this.substring(0, pre.length) == pre;
        },

        allButLast: function() {
                return this.substring(0, this.length - 1);
        }
});

$begins = function(prefixes) {
        prefixes = $splat(prefixes);

        return function(str) {
                return prefixes.some(function(prefix) {
                        return str.beginsWith(prefix);
                });
        };
};

Array.implement({
        getAllButLast: function() {
                return this.filter(function(value, i) {
                        return i < this.length - 1;
                }.bind(this));
        },

        isEmpty: function() {
                return this.length == 0;
        },

        oneElement: function() {
                if (this.length != 1)
                        shouldntHappen();

                return this[0];
        }
});

Element.implement({
        $$: Element.getElements,

        $we: function(weid) {
                return this.getElement('[weid=' + weid + ']');
        },

	hide: function() {
	        this.setStyle('display', 'none');
	},

	show: function() {
	        this.setStyle('display', '');
	}
});

we.delta = {};

we.submitChanges = function() {
        console.log('Delta submitted');
        console.log(we.delta);

        wave.getState().submitDelta(we.delta);
        we.delta = {};
	we.inTransaction = false;
};

we.startTransaction = function() {
        we.inTransaction = true;
};

we.setMixinName = function(name) {
        if (we.mixinState._name != name)
                we.mixinState.set('_name', name);
};

var FuncArray = function() {
        var result = [];

        result.run = function() {
	        var args = arguments;
	        this.each(function(item) {
		        item.run(args);
	        });
        };

        return result;
};

we.State = new Class({
        initialize: function(cursorPath) {
	        this.$cursorPath = cursorPath;
        },

	getClean: function() {
	        var result = {};
	        var self = this;

	        self.getKeys().each(function(key) {
		        var val = self[key];

		        if ($type(val) == 'object')
			        val = val.getClean();
                        
		        result[key] = val;
		});

	        return result;
	},

	getObj: function(key) {
	        if (this[key] == null) {
		        this[key] = new we.State(this.$cursorPath + key + '.');
	        }

	        return this[key];
	},

        set: function(key, value) {
	        this[key] = value;

	        if (!(this.$cursorPath == null)) {
                        var cursorPath = this.$cursorPath;

		        if ($type(value) == 'object')
		                we.flattenState(value, cursorPath + (key ? (key + '.') : ''), we.delta);
                        else
		                we.delta[cursorPath + key] = value;
		        
                        if (!we.inTransaction) {
		                we.submitChanges();
                        }
	        }

	        return this;
        },

        unset: function(key, recursive) {
                var oldValue = this[key];

                if ($type(oldValue) == 'object') {
                        oldValue.getKeys().each(function(subkey) {
	                        oldValue.unset(subkey, true);
                        });
                }
                else {
                        we.delta[this.$cursorPath + key] = null;
                }

                if (!we.inTransaction && !recursive) {
                        we.submitChanges();
                }

                return this;
        },

	getKeys: function() {
	        var result = [];

	        for (var x in this) 
		        if (x != 'caller' && x != '_current' && x != '_context' && !(x.beginsWith('$')) && !(this[x] instanceof Function)) /* $fix? */
		                result.push(x);

	        return result;
	},

        adjustCursorPath: function(newCursorPath) {
                var result = new we.State(newCursorPath);
                var self = this;

                self.getKeys().each(function(key) {
                        var val = self[key];
                        if ($type(val) == 'object') {
                                result[key] = val.adjustCursorPath(newCursorPath + key + '.');
                        }
                        else {
                                result[key] = val;
                        }
                });

                return result;
        },

	////////////////////////////
	// Elastic List Functions //
	////////////////////////////
	asArray: function() {
	        var self = this;

                return self.getKeys().filter($not($begins('_'))).sort(function(a, b) {
		        return parseInt(self[a]._position) > parseInt(self[b]._position) ? 1 : -1;
		}).map(function(key) {
			var result = self[key];
                        result._id = key;
                        return result;
		});
	},

	each: function(f) {
	        this.asArray().each(f);
	},

        remove: function(el) {
                this.unset(el._id);
        },

	insertAtPosition: function(pos, val) {
	        var itemId = '' + $random(0, 100000000);
	        this.set(itemId, $merge({_position: '' + pos}, val));
	        return this.$cursorPath + itemId;
	},

	append: function(val) {
	        val = val || {};
	        var self = this;
	        var newPosition = between(self.getKeys().map(function(key) { return parseInt(self[key]._position) }).max(), 100000000000);
	        return self.insertAtPosition(newPosition, val);
	}
});

between = function(x, y) {
        if (x == -Infinity)
	        x = 0;

        return $random(x, y);
};


Hash.implement({
        filterKeys: function(filter) {
                return this.filter(function(value, key) {
                            return filter(key);
                });
        }
});

we.deepenState = function(state) {
        var result = new we.State('');

        $H(state).filterKeys($not($begins('$'))).each(function(value, key) {
                var cursor = result;
                var tokens = key.split('.');
                var cursorPath = '';

                tokens.getAllButLast().each(function(token) {
                        cursorPath += token + '.';

                        if (!cursor[token]) {
			        cursor[token] = new we.State(cursorPath);
                        }

                        cursor = cursor[token];
                });

                var endVal = cursor[tokens.getLast()] = value;
        });

        return result;
};

we.flattenState = function(state, cursorPath, into) {
        cursorPath = cursorPath || '';
        into = into || {};

        $H(state).filterKeys($not($begins('$'))).each(function(value, key) {
                if ($type(value) == 'object')
                        we.flattenState(value, cursorPath + key + '.', into);
                else
                        into[cursorPath + key] = value;
        });

        return into;
};

we.computeState = function() {
        var waveState = wave.getState();

        if (waveState) {
                we.rawState = waveState.state_;
                return we.state = we.deepenState(we.rawState);
        }
};

modeChanged = new FuncArray();

function weModeChanged() {
        if (typeof modeChanged != 'undefined') {
	        modeChanged.run(we.lastMode, wave.getMode());
	        we.lastMode = wave.getMode();
	        gadgets.window.adjustHeight();
        }
}

we.$ = function(id) {
        return we.el.getElementById(id);
};

we.mixinFuncs = {};

we.applyMixinsToElement = function(mixins, el) {
        var baseMixinCtxByName = {};

        mixins.each(function(mixinState) {
	        if (mixinState._code) {
		        we.mixinCtx = mixinState._context = {state: mixinState, el: el};
		        we.mixinState = mixinState;
		        we.el = el;	       

		        if (we.mixinState._name) {
		                baseMixinCtxByName[we.mixinState._name] = we.mixinCtx;
		        }
/*
                        if (!we.mixinFuncs[we.mixinState._name])
		                eval('we.mixinFuncs[we.mixinState._name] = function() {' + we.mixinState._code + '};');

                        we.mixinFuncs[we.mixinState._name]();
                        */
                    
                    eval(we.mixinState._code);
	        }
	});
};

msg = null;
debug = false;

function debugState() {
	if (debug) {
	        if (!msg)  {
		        msg = new gadgets.MiniMessage("http://wave.thewe.net/gadgets/thewe-ggg/thewe-ggg.xml", $('messageBox'));
	        }

	        // for debug
	        msg.createDismissibleMessage(JSON.stringify(we.rawState));
	}
}

function weStateUpdated() {
        var startTime = $time();

        state = we.computeState();

	/* $fix - see what actually changed */
        if (we.mixins != state._mixins) {
                we.mixins = state._mixins;
		$('content').empty();
		modeChanged.empty();
		we.applyMixinsToElement(we.mixins, $('content'));
                weModeChanged();
        }
	
	debugState();
        gadgets.window.adjustHeight();
        console.log('Render time: ' + ($time() - startTime) + 'ms');
}

we.fetchMixin = function(mixinId, mixinName) {
        we.state.set('mixin-rep-key', JSON.stringify({
                key: mixinId, 
                mixinName: mixinName
        }));
};

function main() {
        if (wave && wave.isInWaveContainer()) {
                window.addEvent('keypress', function(event) {
                        if (event.alt && event.control) {
	                        var key = String.fromCharCode(event.event.charCode);

	                        if (key == 's') {
				        console.log(js_beautify(JSON.stringify(we.state.getClean()), {indent_size: 4, indent_char: ' ', preserve_newlines: false}));
	                        }

	                        if (key == 'o') {
	                                wave.getState().submitValue(
	                                        prompt("Key"),
	                                        prompt("Value"));
	                        }

                                if (key == 'c') {                         
                                        wave.getState().submitValue('from-key', '_mixins');
                                        alert('Prototype chosen');
                                }

				if (key == 'e') {
				        alert(eval(prompt("eval")));
				}

				if (key == 'b') {
				        debug = !debug;
				        debugState();
				}

                                if (key == 'j') {
                                        we.state.set('blip-rep-keys', (we.state._mixins.asArray().map(function(x) {
                                                return x.$cursorPath + '_code'; // $fix horrible .code $cursorPath
                                        }).join()));
                                }

				if (key == 'm') {
				        we.startTransaction();

				        var mixinName = prompt("Use an existing mixin? If so, what is its name?");

				        if (!we.state._mixins)
					        we.state.set('_mixins', new we.State('_mixins.')); // $fix - should this be {} instead of new we.State()?

                                        var createNewMixinId = function() {
                                                // $fix what is this crazy + '._code' thing?!
                                                return we.state._mixins.append() + '._code';
                                        };

                                        if (mixinName) {
                                                var filtered = we.state._mixins.asArray().filter(function(x) {return x._name == mixinName;});                               

                                                if (filtered.isEmpty()) {
                                                        // Mixin with requested name doesn't exist - add it
                                                        we.fetchMixin(createNewMixinId(), mixinName);                      
                                                }
                                                else {
                                                        // Mixin found - replace it with new version
                                                        var mixin = filtered.oneElement();
                                                        we.fetchMixin(mixin.$cursorPath + '_code', mixinName);
                                                }             
                                        } else {
					        we.state.set('blip-rep-keys', createNewMixinId());
				        }

				        we.submitChanges();
				}
                            
                                if (key == 't') {
                                        we.state.set('to-key', '_');
                                        alert('Choose origin by going to the right view and pressing Ctrl-Alt-R');
                                }
                            
                                if (key == 'r') {
                                        var xkeys = prompt('Which mixins or mixin fragments? (comma-separated)');

                                        var fromKeys = [];
                                        xkeys.split(',').each(function(xkey) {
                                                var cursor = we.state._mixins;
                                                var newCursor;
                                                var cursorPath = '_mixins.';
                                                var newCursorPath;

                                                xkey.split('.').each(function(x) {
                                                        if (cursor[x]) {
                                                                newCursor = cursor[x];
                                                                newCursorPath = cursorPath + x + '.';
                                                        }
                                                        else {
                                                                cursor.getKeys().each(function(candidate) {
                                                                        if (cursor[candidate]._name == x) {
                                                                                newCursor = cursor[candidate];
                                                                                newCursorPath = cursorPath + candidate + '.';
                                                                        }

                                                                        // $fix: make good each() on we.State
                                                                        
                                                                });
                                                        }

                                                        cursor = newCursor;
                                                        cursorPath = newCursorPath;
                                                });

                                                fromKeys.push(cursorPath.allButLast());
                                                // $fix: write good trim, rtrim, ltrim
                                        });

                                        var fromKey = fromKeys.join();
                                        alert('from-key: ' + fromKey);
                                        we.state.set('from-key', fromKey);
                                }
                        }
                });

                wave.setModeCallback(weModeChanged);
                wave.setStateCallback(weStateUpdated);
        }
};

gadgets.util.registerOnLoadHandler(main);


