/**
 *  ****************  Who Took The Car  ****************
 *
 *  Design Usage:
 *  This was designed to indicate who took the shared car..... 
 *  It was created as a response to the creation of a special DTH:
 *  This DTH was originally developed by SmartThings for a garage door, 
 *  then modified by Robin Winbourne for use with a dog feeder to give access to four 'states'.
 *  Then modified by me (@cobra) to change the text/colours/icons to be able to use it to show who took a shared car :)
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
 *  Last Update: 10/08/2017
 *
 *  Changes:
 *
 *  V1.2.0 - Added driver's own car 
 *  V1.1.1 - Debug & Typos
 *  V1.1.0 - Added Enable/Disable switch - Added paragraph & header
 *  V1.0.0 - POC
 */
 
 
 
 
 
 
definition(
    name: "Which Car?",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Sets a switch when a car leaves, and tries to work out who took it",
    category: "Family",
   iconUrl: "http://54.246.165.27/img/icons/car.png",
	iconX2Url: "http://54.246.165.27/img/icons/car.png",
    iconX3Url: "http://54.246.165.27/img/icons/car.png",
)

preferences {


section("") {
        paragraph "V1.2.0"
       paragraph image: "http://54.246.165.27/img/icons/cobra3.png",
                  title: "Who Took The Car?",
                  required: false,
                  "This app is designed to use a special 'Virtual Switch' to indicate who left with a shared vehicle"
    }

 section(){
            input "enableApp", "bool", title: "Enable App", required: true, defaultValue: true
        }




	section() {
		input "car1", "capability.presenceSensor", title: "Car 1 Presence Sensor", multiple: false, required: true
   		input "car2", "capability.presenceSensor", title: "Car 2 Presence Sensor", multiple: false, required: true
      	input "car3", "capability.presenceSensor", title: "Car 3 Presence Sensor", multiple: false, required: true
    }
    
     section("Select Driver 1 "){
     input "carDriver1", "capability.presenceSensor", title: "Driver 1's Presence Sensor", multiple: false, required: true
    }
     section("Select Driver 2"){
     input "carDriver2", "capability.presenceSensor", title: "Driver 2's Presence Sensor", multiple: false, required: true
    }
     section("Select Driver Status Indicator Switch"){
     input "switch1", "capability.doorControl", title: "Driver 1 Virtual Presence Status", multiple: false, required: true
     input "switch2", "capability.doorControl", title: "Driver 2 Virtual Presence Status", multiple: false, required: true
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
// Check if app is enabled
appEnable()

	subscribe(car1, "presence", "car1Handler")
    subscribe(car2, "presence", "car2Handler")
    subscribe(car3, "presence", "car3Handler")
    subscribe(carDriver1, "presence", "driver1Handler")
    subscribe(carDriver2, "presence", "driver2Handler")
 
    
}

def car1Handler(evt){
state.d1car = evt.value
log.debug "$car1 = $state.d1car"

if (state.d1car  == "present") { 
state.d1carStatus = 'not taken'
log.info "Car 1 = $state.d1carStatus"
}


}

def car2Handler(evt){
state.d2car = evt.value
log.debug "$car2 = $state.d2car"

if (state.d2car  == "present") { 
state.d2carStatus = 'not taken'
log.info "Car 2 = $state.d2carStatus"
}
}

def car3Handler(evt){
state.d3car = evt.value
log.debug "$car3 = $state.d3car"

if (state.d3car  == "present") { 
state.d3carStatus = 'not taken'
log.info "Car 3 = $state.d3carStatus"
}
}

def driver1Handler(evt) {
   state.driver1 = evt.value 
 log.debug "$carDriver1 = $state.driver1"
 if (state.driver1  == "present") { 
 log.info " Driver 1 arrived so setting at home"
 switch1.at_home()
 }
 
  if (state.driver1  == "not present") { 
  log.info " Driver 1 left so waiting 10 seconds then processing"
runIn(10, processDriver1) 
 }
 
}
def driver2Handler(evt) {
   state.driver2 = evt.value  
   log.debug "$carDriver2 = $state.driver2"
   
		if (state.driver2  == "present") { 
         log.info " Driver 2 arrived so setting at home"
  switch2.at_home()
    }
    
		if (state.driver2  == "not present") { 
         log.info " Driver 2 left so waiting 10 seconds then processing"
runIn(10, processDriver2) 
 }    
    
}

 
 
 
 def processDriver1(){ 
 
	if (state.appGo == true && state.d1car == "not present" && state.d1carStatus == 'not taken') { 
 	switch1.car1()
	state.d1carStatus = 'taken'
 log.debug "$carDriver1 took $car1"
 
} 
	if (state.appGo == true && state.d2car == "not present" && state.d2carStatus == 'not taken') { 
	switch1.car2()
	state.d2carStatus = 'taken'
  log.debug "$carDriver1 took $car2"
} 
	if (state.appGo == true && state.d3car == "not present" && state.d3carStatus == 'not taken') { 
	switch1.car3()
	state.d3carStatus = 'taken'
  log.debug "$carDriver1 took $car3"
} 
}
 def processDriver2(){ 
 
	if (state.appGo == true && state.d1car == "not present" && state.d1carStatus == 'not taken') { 
 	switch2.car1()
	state.d1carStatus = 'taken'
  log.debug "$carDriver2 took $car1"
 
} 
	if (state.appGo == true && state.d2car == "not present" && state.d2carStatus == 'not taken') { 
	switch2.car2()
	state.d2carStatus = 'taken'
  log.debug "$carDriver2 took $car2"
} 
	if (state.appGo == true && state.d3car == "not present" && state.d3carStatus == 'not taken') { 
	switch2.car3()
	state.d3carStatus = 'taken'
  log.debug "$carDriver2 took $car3"
} 

}



 
 
 
 
 // Enable/Disable App
def appEnable (){
	if (enableApp == true){ 
    state.appGo = true
    log.debug "App is Enabled" }
    else if (enableApp == false){ 
    state.appGo = false
    log.debug "App is Disabled" }
    
 }