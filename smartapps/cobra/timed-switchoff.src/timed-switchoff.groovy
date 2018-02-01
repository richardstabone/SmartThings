/**
 *  ****************  Timed SwitchOff  ****************
 *
 *  Design Usage:
 *  This was designed to turn off an outlet/light/switch a number of seconds after turning on
 *  Originally created to work a garage door momentary switch
 *
 *  Copyright 2018 Andrew Parker
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
 *  Last Update: 01/02/2018
 *
 *  Changes:
 *
 *
 *  V1.0.1 - Debug for Android issue not showing selectable devices
 *  V1.0.0 - POC
 *
 */
 
 
definition(
    name: "Timed SwitchOff",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Turn off an outlet/light/switch a number of seconds after turning on",
    category: "Green Living",
    iconUrl: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    iconX2Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    iconX3Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png")


preferences {
	section("") {
    
    paragraph "V1.0.1"
     paragraph image:  "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
       	title: "Timed SwitchOff",
        required: false, 
    	"Turn off an outlet/light/switch a number of seconds after turning on"
    }
    section("When you turn this switch on") {
		  input "switch1", "capability.switch", multiple: false
          }
       section(" Turn it back off after this number of seconds"){   
          input "delay1", "number"
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
	 subscribe(switch1, "switch", switch1Handler)
}


def switch1Handler(evt){
// log.debug " switch1Handler called..."
state.switchNow = evt.value
def myDelay = delay1
if (state.switchNow == 'on'){
log.info "$switch1 is $state.switchNow - Turning off in $myDelay seconds"
runIn(myDelay, turnOff)
	}
    
else if (state.switchNow == 'off'){
  log.info "$switch1 is $state.switchNow"  
    }
}




def turnOff(){
log.info " Switching $switch1 off"
switch1.off()
}


def setAppVersion(){
    state.appversion = "1.0.1"
}