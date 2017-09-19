/**
 *  ****************  It's Raining - Check Open Windows  ****************
 *
 *  Design Usage:
 *  This was designed to check a rain sensor and announce if any windows are open when it rains
 *
 *
 *  Copyright 2017 Andrew Parker
 *  
 *  This SmartApp is free!
 *  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://www.paypal.me/smartcobra
 *  
 *
 *  I'm very happy for you to use this app without a donation, but if you find it useful then it would be nice to get a 'shout out' on the forum! -  @Cobra
 *  Have an idea to make this app better?  - Please let me know :)
 *
 *  Website: http://securendpoint.com/smartthings
 *
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *-------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @Cobra
 *
 *-------------------------------------------------------------------------------------------------------------------
 *
 *  Last Update: 23/08/2017
 *
 *  Changes:
 *
 *  V1.3.0 - Custom Icons
 *  V1.2.0 - Added configurable delay between message events
 *  V1.1.0 - Added ability to customise alert message
 *  V1.0.1 - Debug
 *  V1.0.0 - POC
 *
 */
 
 
 
 
definition(
    name: "It's Raining - Check Open Windows",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "If a sensor detects rain, then announce any open windows & doors (Contact Sensors)",
    category: "",
    iconUrl: "http://54.246.165.27/img/icons/rain.png",
    iconX2Url: "http://54.246.165.27/img/icons/rain.png",
    iconX3Url: "http://54.246.165.27/img/icons/rain.png",
    )


preferences {


	section() {
   
        paragraph image: "http://54.246.165.27/img/icons/cobra3.png",
                  //       required: false,
                  "Version: 1.3.0 - Brought to you by Cobra"
    }

	section() {
    
        paragraph image: "http://54.246.165.27/img/icons/rain.png",
                  title: "It's Raining - Check Open Windows",
                  required: false,
                  "This app uses a rain sensor to alert if any windows or doors are open"
    }

	
     section("Select Enable/Disable Switch (Optional)") {
    		input "switch1", "capability.switch", title: "", required: false, multiple: false 
    }  
    
		section("Select 'Rain Sensor'"){		
        input "water1", "capability.waterSensor", title: "Select Rain Sensor", required: false, multiple: true
		}  
      section("Speaker Settings") { 
        input "speaker1", "capability.musicPlayer", title: "Choose a speaker", required: true, multiple: true, submitOnChange:true
         input "volume1", "number", title: "Speaker volume", description: "0-100%", required: false
         input "delay1", "number", title: "Delay before speaking (Seconds - enter 0 for no delay)", description: "Seconds", required: true
         input "message1", "text", title: "Message to speak before list of open devices",  defaultValue: "Hey!, It's raining!, and I thought you might like to know that the following windows or doors are open:", required: true
          input "delay2", "number", title: "Number of minutes between messages", description: "Minutes", required: true
          }
 	section("Allow messages between what times?") {
        input "fromTime", "time", title: "From", required: true
        input "toTime", "time", title: "To", required: true
} 
   
    section("'Contact Sensors' to check..."){
		input "sensors", "capability.contactSensor", multiple: true
}
}


def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
state.appversion = ""
setAppVersion()
	subscribe(water1, "water.wet", wetHandler1)
    subscribe(switch1, "switch", switchHandler)
}


def switchHandler(evt) {
   state.currS1 = evt.value  // Note: Optional if switch is used to control action
   log.debug "$switch1 = $evt.value"
  					   }


def wetHandler1 (evt){
log.info " It's raining! - Waiting $delay1 seconds before checking to see if I can play message"

def myDelay1 = delay1
 if (state.currS1 != 'nul' && state.currS1 == "on") {
def between = timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)
    if (between) {

		runIn(myDelay1,talkNow1)
			}
    

        
else{

log.info " It's raining but it's not within the correct time window to say anything"
}
	}
else  if (state.currS1 != 'nul' && state.currS1 == "off") {  
log.info " It's raining but '$switch1' is set to 'Off' so I'm doing as I'm told and keeping quiet!"
}		
}

def talkNow1() {
log.info " Timer = $state.timer"
if (state.timer != 'no'){

def newmsg = message1
// def newmsg = "It's raining ,,, and the following windows or doors, are open:"  //test

log.info " Checking open windows & doors now..."
	if (volume1) {
        		speaker1.setLevel(volume1)
                }
log.info "Speaker(s) in use: $speaker1"        
def open = sensors.findAll { it?.latestValue("contact") == 'open' }
		if (open) { 
        log.trace "Open windows or doors: ${open.join(',,, ')}"
                state.fullMsg1 = "$newmsg  ${open.join(',,, ')}"
            }

		speaker1.speak(state.fullMsg1)
        
	state.timer = 'no'
    
// log.debug "Message allow: set to $state.timer as I have just played a message"
state.timeDelay = 60 * delay2
log.info "Waiting for $state.timeDelay seconds before resetting timer to allow further messages"
runIn(state.timeDelay, resetTimer)

}

	else if (state.timer == 'no'){
	state.timeDelay = 60 * delay2
log.info "Can't speak message yet - too close to last message"
log.info "Waiting for $state.timeDelay seconds before resetting timer"
	runIn(state.timeDelay, resetTimer)
}

}


def resetTimer() {
state.timer = 'yes'
log.info "Timer reset - Messages allowed"

}


// App Version   *********************************************************************************
def setAppVersion(){
    state.appversion = "1.3.0"
}