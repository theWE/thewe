from waveapi import appengine_robot_runner
from waveapi import events
from waveapi import robot

def Proxy(event, wavelet):
    blip = event.blip
        
if __name__ == '__main__':
    thewe = robot.Robot('thewe-1',
                        image_url='http://a3.twimg.com/profile_images/401079957/256px-Circle.svg_bigger.png')

    # Doesn't really do anything - the proxying is done within class Robot
    thewe.register_handler(events.BlipSubmitted, Proxy)
    thewe.register_handler(events.GadgetStateChanged, Proxy)
    thewe.register_handler(events.AnnotatedTextChanged, Proxy, filter='we/eval')
            
    appengine_robot_runner.run(thewe, debug=True)
            
            
            
