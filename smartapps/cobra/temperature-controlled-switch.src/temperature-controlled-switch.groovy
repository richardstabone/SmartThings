/**
 *  Copyright 2015 A J Parker
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
 *  Temperature Controlled Switch
 *
 *  V1.2 - Added action to turn off heating if 'allow' switch turned off
 *  V1.1 - Added days of the week
 *  V1.0 - POC
 *
 *  Author: Cobra
 */
 
definition(
    name: "Temperature Controlled Switch",
    namespace: "Cobra",
    author: "A J Parker",
    description: "Monitor the temperature to turn on/off a switch (Heater?).",
    category: "Convenience",
    iconUrl: "http://54.246.165.27/img/icons/cobra3.png",
    iconX2Url: "http://54.246.165.27/img/icons/cobra3.png",
)

preferences {

section("") {
        paragraph " V1.2.0 "
        paragraph image: "http://54.246.165.27/img/icons/cobra3.png",
                  title: "Temperature Controlled Switch",
                  required: false,
                  "This SmartApp was designed to control a heater - turning on/off with  varying temperatures. It also has an optional 'override' switch"
    }

	section("Enable/Disable Control") {
		input "switch1", "capability.switch", required: True, title: "Control Switch"
     }   
	section("Select Temperature Sensor") {
		input "temperatureSensor1", "capability.temperatureMeasurement" , required: true
	}
	section("Desired Temperature") {
		input "temperature1", "number", title: "Temperature?", required: true
	}
   	section("Control this switch/heater...") {
		input "switch2", "capability.switch", required: true
	}
     section("On Which Days") {
        input "days", "enum", title: "Select Days of the Week", required: true, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
    }
}


def installed() {
log.debug "Installed with settings: ${settings}"
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
    subscribe(switch1, "switch", switch1Handler)
    state.currS1 == "on" 
}

def updated() {
log.debug "Updated with settings: ${settings}"
	unsubscribe()
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
    subscribe(switch1, "switch", switch1Handler)
    state.currS1 == "on"
  }


def switch1Handler(evt){
   state.currS1 = evt.value 
   log.trace " $switch1 is $state.currS1"
   if (state.currS1 == "off" ) {
   switch2.off()
   }
}




def temperatureHandler(evt) {
	
	log.debug "temperature: $evt.value, $evt"
	if (state.currS1 != "off" ) {
   log.debug "Temperature has changed and $switch1 is on..."

 def df = new java.text.SimpleDateFormat("EEEE")
      df.setTimeZone(location.timeZone)
    def day = df.format(new Date())
    //Is today a day to run?
    def dayCheck = days.contains(day)
    if (dayCheck) {

log.trace "Today is a working day so we can continue..."

	def myTemp = temperature1
	// Is reported temp below or above setting?	
	if (evt.doubleValue < myTemp) {
		
		log.debug "Reported temperature is below $myTemp so activating $switch2"
			switch2.on()
		}
        else if (evt.doubleValue >= myTemp) {
        log.debug "Reported temperature is equal to, or above, $myTemp so deactivating $switch2"
			switch2.off()
	}
    }
     else {
 log.debug " Temperature has changed but auto switching is not enabled for today"
 }
 }
    else {
    log.debug "Temperature has changed but control switch: $switch1 is 'off' so auto switching is not enabled"
}
}
 

