(ns we
  (:require clojure.test)
  (:use clojure.test))

(def null nil)

(def incoming-map-without-gadget
  {"blips"  {
             "map"  {
                     "b+2ZbR8dl4D"  {
                                     "lastModifiedTime"  1254308168017,
                                     "contributors"  {
                                                      "javaClass"  "java.util.ArrayList",
                                                      "list"  [
                                                               "ayal@wavesandbox.com",
                                                               "thewe-experiments@appspot.com"
                                                               ]
                                                      },
                                     "waveletId"  "wavesandbox.com!conv+root",
                                     "waveId"  "wavesandbox.com!w+2ZbR8dl4C",
                                     "parentBlipId"  null,
                                     "version"  13,
                                     "creator"  "ayal@wavesandbox.com",
                                     "content"  "CONTENT",
                                     "blipId"  "b+2ZbR8dl4D",
                                     "javaClass"  "com.google.wave.api.impl.BlipData",
                                     "annotations"  {
                                                     "javaClass"  "java.util.ArrayList",
                                                     "list"  [
                                                              {
                                                               "range"  {
                                                                         "start"  -1,
                                                                         "javaClass"  "com.google.wave.api.Range",
                                                                         "end"  1
                                                                         },
                                                               "name"  "conv/title",
                                                               "value"  "",
                                                               "javaClass"  "com.google.wave.api.Annotation"
                                                               }
                                                              ]
                                                     },
                                     "elements"  {
                                                  "map"  {},
                                                  "javaClass"  "java.util.HashMap"
                                                  },
                                     "childBlipIds"  {
                                                      "javaClass"  "java.util.ArrayList",
                                                      "list"  [
                                                               ]
                                                      }
                                     }
                     },
             "javaClass"  "java.util.HashMap"
             },
   "robotAddress"  "thewe0@appspot.com",
   "events"  {
              "javaClass"  "java.util.ArrayList",
              "list"  [
                       {
                        "timestamp"  1254308174642,
                        "modifiedBy"  "ayal@wavesandbox.com",
                        "javaClass"  "com.google.wave.api.impl.EventData",
                        "properties"  {
                                       "map"  {
                                               "blipId"  "b+2ZbR8dl4D",
                                               "participantsRemoved"  {
                                                                       "javaClass"  "java.util.ArrayList",
                                                                       "list"  [
                                                                                ]
                                                                       },
                                               "participantsAdded"  {
                                                                     "javaClass"  "java.util.ArrayList",
                                                                     "list"  [
                                                                              "thewe0@appspot.com"
                                                                              ]
                                                                     }
                                               },
                                       "javaClass"  "java.util.HashMap"
                                       },
                        "type"  "WAVELET_SELF_ADDED"
                        }
                       ]
              },
   "wavelet"  {
               "lastModifiedTime"  1254308174642,
               "title"  "",
               "waveletId"  "wavesandbox.com!conv+root",
               "rootBlipId"  "b+2ZbR8dl4D",
               "javaClass"  "com.google.wave.api.impl.WaveletData",
               "dataDocuments"  {
                                 "map"  {
                                         },
                                 "javaClass"  "java.util.HashMap"
                                 },
               "creationTime"  1254308144237,
               "waveId"  "wavesandbox.com!w+2ZbR8dl4C",
               "participants"  {
                                "javaClass"  "java.util.ArrayList",
                                "list"  [
                                         "ayal@wavesandbox.com",
                                         "thewe-experiments@appspot.com",
                                         "thewe0@appspot.com"
                                         ]
                                },
               "creator"  "ayal@wavesandbox.com",
               "version"  15
               }
   })


(def incoming-map-without-gadget-blip-submitted 
  (assoc-in incoming-map-without-gadget ["events" "list" 0 "type"] "BLIP_SUBMITTED"))

(def incoming-map-eval
  (assoc-in incoming-map-without-gadget-blip-submitted ["blips" "map" "b+2ZbR8dl4D" "content"] "[;(create-child-blip);]"))

(def incoming-map-with-gadget
  {"blips"  {
             "map"  {
                     "b+2ZbR8dl4D"  {
                                     "lastModifiedTime"  1254308168017,
                                     "contributors"  {
                                                      "javaClass"  "java.util.ArrayList",
                                                      "list"  [
                                                               "ayal@wavesandbox.com",
                                                               "thewe-experiments@appspot.com"
                                                               ]
                                                      },
                                     "waveletId"  "wavesandbox.com!conv+root",
                                     "waveId"  "wavesandbox.com!w+2ZbR8dl4C",
                                     "parentBlipId"  null,
                                     "version"  13,
                                     "creator"  "ayal@wavesandbox.com",
                                     "content"  " ",
                                     "blipId"  "b+2ZbR8dl4D",
                                     "javaClass"  "com.google.wave.api.impl.BlipData",
                                     "annotations"  {
                                                     "javaClass"  "java.util.ArrayList",
                                                     "list"  [
                                                              {
                                                               "range"  {
                                                                         "start"  -1,
                                                                         "javaClass"  "com.google.wave.api.Range",
                                                                         "end"  1
                                                                         },
                                                               "name"  "conv/title",
                                                               "value"  "",
                                                               "javaClass"  "com.google.wave.api.Annotation"
                                                               }
                                                              ]
                                                     },
                                     "elements"  {
                                                  "map"  {
                                                          "0"  {
                                                                "javaClass"  "com.google.wave.api.Gadget",
                                                                "properties"  {
                                                                               "map"  {
                                                                                       "_view"  "stateUpdated = function(){};",
                                                                                       "KEY"  "XXX",
                                                                                       "url"  "http //wave.thewe.net/gadgets/thewe-ggg/thewe-ggg.xml"
                                                                                       },
                                                                               "javaClass"  "java.util.HashMap"
                                                                               },
                                                                "type"  "GADGET"
                                                                }
                                                          },
                                                  "javaClass"  "java.util.HashMap"
                                                  },
                                     "childBlipIds"  {
                                                      "javaClass"  "java.util.ArrayList",
                                                      "list"  [
                                                               ]
                                                      }
                                     }
                     },
             "javaClass"  "java.util.HashMap"
             },
   "robotAddress"  "thewe0@appspot.com",
   "events"  {
              "javaClass"  "java.util.ArrayList",
              "list"  [
                       {
                        "timestamp"  1254308174642,
                        "modifiedBy"  "ayal@wavesandbox.com",
                        "javaClass"  "com.google.wave.api.impl.EventData",
                        "properties"  {
                                       "map"  {
                                               "blipId"  "b+2ZbR8dl4D",
                                               "participantsRemoved"  {
                                                                       "javaClass"  "java.util.ArrayList",
                                                                       "list"  [
                                                                                ]
                                                                       },
                                               "participantsAdded"  {
                                                                     "javaClass"  "java.util.ArrayList",
                                                                     "list"  [
                                                                              "thewe0@appspot.com"
                                                                              ]
                                                                     }
                                               },
                                       "javaClass"  "java.util.HashMap"
                                       },
                        "type"  "BLIP_SUBMITTED"
                        }
                       ]
              },
   "wavelet"  {
               "lastModifiedTime"  1254308174642,
               "title"  "",
               "waveletId"  "wavesandbox.com!conv+root",
               "rootBlipId"  "b+2ZbR8dl4D",
               "javaClass"  "com.google.wave.api.impl.WaveletData",
               "dataDocuments"  {
                                 "map"  {
                                         },
                                 "javaClass"  "java.util.HashMap"
                                 },
               "creationTime"  1254308144237,
               "waveId"  "wavesandbox.com!w+2ZbR8dl4C",
               "participants"  {
                                "javaClass"  "java.util.ArrayList",
                                "list"  [
                                         "ayal@wavesandbox.com",
                                         "thewe-experiments@appspot.com",
                                         "thewe0@appspot.com"
                                         ]
                                },
               "creator"  "ayal@wavesandbox.com",
               "version"  15
               }
   })


(defn run-test [gadget-db rep-rules incoming-map]
  (rep-ops-to-outgoing-map gadget-db
    ; @todo why do we need to write this code here and in server.clj
    (concat
      (do-repl (incoming-map-to-rep-ops incoming-map))
      (do-replication rep-rules (incoming-map-to-rep-ops incoming-map)))))

(def rep-rules1 #{#{{
                    :type "blip"
                    :blip-id "b+2ZbR8dl4D"
                    :wave-id "wavesandbox.com!w+2ZbR8dl4C"
                    :wavelet-id "wavesandbox.com!conv+root"
                    }
                   {
                    :type "blip"
                    :blip-id "blippy"
                    :wave-id "wavey"
                    :wavelet-id "wavelety"
                    }
                   {
                    :type "gadget"
                    :blip-id "b+2ZbR8dl4D"
                    :wave-id "wavesandbox.com!w+2ZbR8dl4C"
                    :wavelet-id "wavesandbox.com!conv+root"
                    :key "KEY"
                    }
                   {
                    :type "gadget"
                    :blip-id "BLIP GADGET"
                    :wave-id "BLIP WAVE"
                    :wavelet-id "BLIP WAVELET"
                    :key "BLIP KEY"
                    }

                    }})

(is
  (= (run-test {} rep-rules1 incoming-map-without-gadget)
  {"javaClass" "com.google.wave.api.impl.OperationMessageBundle", "operations" {"javaClass" "java.util.ArrayList", "list" '()}, "version" "103"}))

(is
  (= (do-replication rep-rules1 (incoming-map-to-rep-ops incoming-map-without-gadget))
  '()))

(is
  (= (incoming-map-to-rep-ops incoming-map-without-gadget)
  '()))

(is
  (= (run-test {} rep-rules1 incoming-map-without-gadget-blip-submitted)
  (read-string
    "{\"javaClass\" \"com.google.wave.api.impl.OperationMessageBundle\", \"operations\" {\"javaClass\" \"java.util.ArrayList\", \"list\" ({\"index\" 1, \"waveletId\" \"BLIP WAVELET\", \"blipId\" \"BLIP GADGET\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" {\"javaClass\" \"com.google.wave.api.Gadget\", \"properties\" {\"map\" {\"BLIP KEY\" \"CONTENT\"}, \"javaClass\" \"java.util.HashMap\"}, \"type\" \"GADGET\"}, \"waveId\" \"BLIP WAVE\", \"type\" \"DOCUMENT_ELEMENT_REPLACE\"} {\"index\" -1, \"waveletId\" \"wavelety\", \"blipId\" \"blippy\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" nil, \"waveId\" \"wavey\", \"type\" \"DOCUMENT_DELETE\"} {\"index\" 0, \"waveletId\" \"wavelety\", \"blipId\" \"blippy\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" \"CONTENT\", \"waveId\" \"wavey\", \"type\" \"DOCUMENT_APPEND\"} {\"index\" 1, \"waveletId\" \"wavesandbox.com!conv+root\", \"blipId\" \"b+2ZbR8dl4D\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" {\"javaClass\" \"com.google.wave.api.Gadget\", \"properties\" {\"map\" {\"KEY\" \"CONTENT\"}, \"javaClass\" \"java.util.HashMap\"}, \"type\" \"GADGET\"}, \"waveId\" \"wavesandbox.com!w+2ZbR8dl4C\", \"type\" \"DOCUMENT_ELEMENT_REPLACE\"})}, \"version\" \"103\"}"
    )))

(is
  (= (do-replication rep-rules1 (incoming-map-to-rep-ops incoming-map-without-gadget-blip-submitted))
  '({:rep-loc {:type "gadget", :blip-id "BLIP GADGET", :wave-id "BLIP WAVE", :wavelet-id "BLIP WAVELET", :key "BLIP KEY"}, :content "CONTENT"} {:rep-loc {:type "blip", :blip-id "blippy", :wave-id "wavey", :wavelet-id "wavelety"}, :content "CONTENT"} {:rep-loc {:type "gadget", :blip-id "b+2ZbR8dl4D", :wave-id "wavesandbox.com!w+2ZbR8dl4C", :wavelet-id "wavesandbox.com!conv+root", :key "KEY"}, :content "CONTENT"})))

(is (=
  (incoming-map-to-rep-ops incoming-map-without-gadget-blip-submitted)
  (read-string
    "({:rep-loc {:type \"blip\", :wave-id \"wavesandbox.com!w+2ZbR8dl4C\", :wavelet-id \"wavesandbox.com!conv+root\", :blip-id \"b+2ZbR8dl4D\"}, :content \"CONTENT\"})"
    )))

(is (=
  (run-test {{
                    :blip-id "b+2ZbR8dl4D"
                    :wave-id "wavesandbox.com!w+2ZbR8dl4C"
                    :wavelet-id "wavesandbox.com!conv+root"
                    } {"A" "B", "C" "D"}}
    rep-rules1 incoming-map-without-gadget-blip-submitted)
  (read-string
    "{\"javaClass\" \"com.google.wave.api.impl.OperationMessageBundle\", \"operations\" {\"javaClass\" \"java.util.ArrayList\", \"list\" ({\"index\" 1, \"waveletId\" \"BLIP WAVELET\", \"blipId\" \"BLIP GADGET\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" {\"javaClass\" \"com.google.wave.api.Gadget\", \"properties\" {\"map\" {\"BLIP KEY\" \"CONTENT\"}, \"javaClass\" \"java.util.HashMap\"}, \"type\" \"GADGET\"}, \"waveId\" \"BLIP WAVE\", \"type\" \"DOCUMENT_ELEMENT_REPLACE\"} {\"index\" -1, \"waveletId\" \"wavelety\", \"blipId\" \"blippy\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" nil, \"waveId\" \"wavey\", \"type\" \"DOCUMENT_DELETE\"} {\"index\" 0, \"waveletId\" \"wavelety\", \"blipId\" \"blippy\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" \"CONTENT\", \"waveId\" \"wavey\", \"type\" \"DOCUMENT_APPEND\"} {\"index\" 1, \"waveletId\" \"wavesandbox.com!conv+root\", \"blipId\" \"b+2ZbR8dl4D\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" {\"javaClass\" \"com.google.wave.api.Gadget\", \"properties\" {\"map\" {\"KEY\" \"CONTENT\", \"A\" \"B\", \"C\" \"D\"}, \"javaClass\" \"java.util.HashMap\"}, \"type\" \"GADGET\"}, \"waveId\" \"wavesandbox.com!w+2ZbR8dl4C\", \"type\" \"DOCUMENT_ELEMENT_REPLACE\"})}, \"version\" \"103\"}"
    )))

(is (=
  (run-test {{
                    :blip-id "b+2ZbR8dl4D"
                    :wave-id "wavesandbox.com!w+2ZbR8dl4C"
                    :wavelet-id "wavesandbox.com!conv+root"
                    } {"A" "B", "C" "D"}}
    rep-rules1 incoming-map-with-gadget)
  (read-string
    "{\"javaClass\" \"com.google.wave.api.impl.OperationMessageBundle\", \"operations\" {\"javaClass\" \"java.util.ArrayList\", \"list\" ({\"index\" 1, \"waveletId\" \"BLIP WAVELET\", \"blipId\" \"BLIP GADGET\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" {\"javaClass\" \"com.google.wave.api.Gadget\", \"properties\" {\"map\" {\"BLIP KEY\" \"XXX\"}, \"javaClass\" \"java.util.HashMap\"}, \"type\" \"GADGET\"}, \"waveId\" \"BLIP WAVE\", \"type\" \"DOCUMENT_ELEMENT_REPLACE\"} {\"index\" -1, \"waveletId\" \"wavelety\", \"blipId\" \"blippy\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" nil, \"waveId\" \"wavey\", \"type\" \"DOCUMENT_DELETE\"} {\"index\" 0, \"waveletId\" \"wavelety\", \"blipId\" \"blippy\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" \"XXX\", \"waveId\" \"wavey\", \"type\" \"DOCUMENT_APPEND\"} {\"index\" -1, \"waveletId\" \"wavesandbox.com!conv+root\", \"blipId\" \"b+2ZbR8dl4D\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" nil, \"waveId\" \"wavesandbox.com!w+2ZbR8dl4C\", \"type\" \"DOCUMENT_DELETE\"} {\"index\" 0, \"waveletId\" \"wavesandbox.com!conv+root\", \"blipId\" \"b+2ZbR8dl4D\", \"javaClass\" \"com.google.wave.api.impl.OperationImpl\", \"property\" \"XXX\", \"waveId\" \"wavesandbox.com!w+2ZbR8dl4C\", \"type\" \"DOCUMENT_APPEND\"})}, \"version\" \"103\"}"
    )))

(is (=
  (incoming-map-to-rep-ops incoming-map-with-gadget)
      '({:rep-loc {:key "_view", :type "gadget", :wave-id "wavesandbox.com!w+2ZbR8dl4C", :wavelet-id "wavesandbox.com!conv+root", :blip-id "b+2ZbR8dl4D"}, :content "stateUpdated = function(){};"} {:rep-loc {:key "KEY", :type "gadget", :wave-id "wavesandbox.com!w+2ZbR8dl4C", :wavelet-id "wavesandbox.com!conv+root", :blip-id "b+2ZbR8dl4D"}, :content "XXX"} {:rep-loc {:key "url", :type "gadget", :wave-id "wavesandbox.com!w+2ZbR8dl4C", :wavelet-id "wavesandbox.com!conv+root", :blip-id "b+2ZbR8dl4D"}, :content "http //wave.thewe.net/gadgets/thewe-ggg/thewe-ggg.xml"})))

(is (=
      (do-replication rep-rules1 (incoming-map-to-rep-ops incoming-map-with-gadget))
      '({:rep-loc {:type "gadget", :blip-id "BLIP GADGET", :wave-id "BLIP WAVE", :wavelet-id "BLIP WAVELET", :key "BLIP KEY"}, :content "XXX"} {:rep-loc {:type "blip", :blip-id "blippy", :wave-id "wavey", :wavelet-id "wavelety"}, :content "XXX"} {:rep-loc {:type "blip", :blip-id "b+2ZbR8dl4D", :wave-id "wavesandbox.com!w+2ZbR8dl4C", :wavelet-id "wavesandbox.com!conv+root"}, :content "XXX"})))

(is (= (do-repl (incoming-map-to-rep-ops incoming-map-eval))
      '({:rep-loc
         {:type "blip",
          :wave-id "wavesandbox.com!w+2ZbR8dl4C",
          :wavelet-id "wavesandbox.com!conv+root",
          :blip-id "b+2ZbR8dl4D"}
         :action "create-child-blip"})))

(comment

  ; @todo MAKE THIS WORK SOMEHOW WITH AYAL'S MAP DIFF FOR RANDOM NUMBERS
(run-test {} #{} incoming-map-eval)
{"javaClass" "com.google.wave.api.impl.OperationMessageBundle",
 "operations"
 {"javaClass" "java.util.ArrayList",
  "list" '(
            {"index" -1,
             "waveletId" "wavesandbox.com!conv+root",
             "blipId" "b+2ZbR8dl4D",
             "javaClass" "com.google.wave.api.impl.OperationImpl",
             "property" {"annotations" {"javaClass" "java.util.ArrayList", "list" []},
                         "childBlipIds" {"javaClass" "java.util.ArrayList", "list" []},
                         "creator" nil,
                         "lastModifiedTime" -1,
                         "blipId" "0.2658112360321232",
                         "waveId" "wavesandbox.com!w+2ZbR8dl4C", "javaClass" "com.google.wave.api.impl.BlipData", "parentBlipId" nil, "elements" {"map" {}, "javaClass" "java.util.HashMap"}, "version" -1, "contributors" {"javaClass" "java.util.ArrayList", "list" []}, "content" "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "waveletId" "wavesandbox.com!conv+root"}, "waveId" "wavesandbox.com!w+2ZbR8dl4C", "type" "BLIP_CREATE_CHILD"})}, "version" "103"}

  )

; @todo how do we automate the addition of unit tests once we believe a function works

