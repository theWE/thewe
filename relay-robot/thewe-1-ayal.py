from waveapi import appengine_robot_runner

from waveapi import element

from waveapi import events

from waveapi import ops

from waveapi import robot

# the robot

sinky = None

def OnSelfAdded(event, wavelet):

    """Invoked when any participants have been added/removed from the wavelet."""

    blip = event.blip

    wavelet.title = 'A wavelet title'

    blip.append(element.Image(url='http://www.google.com/logos/clickortreat1.gif',
                              
                              width=320, height=118))

    wavelet.proxy_for('douwe').reply().append('hi from douwe')
    
    blip.all().delete()
    
    blip.append("NONONO");
    
    blip.all().annotate("kaka", "kiki");
    
    blip.range(2,5).insert("ITS AMAZING");
    
    ggb = element.Gadget(

        'http://wave.thewe.net/gadgets/thewe-ggg/thewe-ggb.xml')

    ggb.waveid = wavelet.wave_id
    
    ggb.kaka = "dfjkgsdf"

    blip.append(ggb)
    
    gaaa = blip.first(element.Gadget,

                      url='http://wave.thewe.net/gadgets/thewe-ggg/thewe-ggb.xml')
    
    gaaa.update_element({'FIE#$^$%^&$%^&@#$%@#$ND': 'y#$%^#$%^#$%^#$%^#$%^es'})

    inlineBlip = blip.insert_inline_blip(5)

    inlineBlip.append('hello again!')

    new_wave = sinky.new_wave(wavelet.domain,

                              wavelet.participants,

                              message=wavelet.serialize())

    new_wave.root_blip.append('A new day and a new wave')

    new_wave.root_blip.append_markup(

        '<p>Some stuff!</p><p>Not the <b>beautiful</b></p>')

    new_wave.submit_with(wavelet)

def OnWaveletCreated(event, wavelet):

    """Called when the robot creates a new wave."""

    org_wavelet = wavelet.robot.blind_wavelet(event.message)

    gadget = element.Gadget(

        'http://kitchensinky.appspot.com/public/embed.xml')

    gadget.waveid = wavelet.wave_id

    org_wavelet.root_blip.append(gadget)

    org_wavelet.root_blip.append('\nInserted a gadget: \xd0\xb0\xd0\xb1\xd0\xb2')

    org_wavelet.submit_with(wavelet)

def OnBlipSubmitted(event, wavelet):

    blip = event.blip

    gadget = blip.first(element.Gadget,

                        url='http://kitchensinky.appspot.com/public/embed.xml')

    if (gadget
        
        and gadget.get('loaded', 'no') == 'yes'
        
        and gadget.get('seen', 'no') == 'no'):
        
        gadget.update_element({'seen': 'yes'})
        
        blip.append('\nSeems all to have worked out.')
        
        image = blip.first(element.Image)
        
        image.update_element({'url': 'http://www.google.com/logos/poppy09.gif'})
        
if __name__ == '__main__':
            
    sinky = robot.Robot('Kitchensinky',
                                
                        image_url='http://kitchensinky.appspot.com/public/avatar.png')
            
    sinky.register_handler(events.WaveletSelfAdded,
                                   
                           OnSelfAdded)
            
    sinky.register_handler(events.WaveletCreated,
                                   
                           OnWaveletCreated)
            
    sinky.register_handler(events.BlipSubmitted, OnBlipSubmitted)
            
    appengine_robot_runner.run(sinky, debug=True)
            
            
            
