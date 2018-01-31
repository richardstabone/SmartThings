/**
 *  Timed SwitchOff
 *
 *  Copyright 2018 Andrew Parker
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
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
    
    paragraph "V1.0.0"
     paragraph image:  "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
       	title: "Timed SwitchOff",
        required: false, 
    	"Turn off an outlet/light/switch a number of seconds after turning on"
    
		  input(name: "switch1", type: "capability.switch", title: "When you turn this switch on", required: true, multiple: false)
          input(name: "delay1", type: "number", title: "Turn it back off after this number of seconds", required: true)
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
    
    if (state.switchNow == 'on'){
  log.info "$switch1 is $state.switchNow"  
    }
}




def turnOff(){
log.info " switching $switch1 off"
switch1.off()

}