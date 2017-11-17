/**
 *  ****************  Switch Follows Water Sensor  ****************
 *
 *  Design Usage:
 *  This was designed to be used with a water sensor to control a switch
 *  Uses a water sensor to receive commands and converts that to switch actions
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
 *  Last Update: 17/11/2017
 *
 *  Changes:
 *
 *  
 * 	
 *  
 *  
 *  V1.0.0 - POC
 *
 */
 
 
 
 
definition(
    name: "Water Sensor to Switch",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Uses a water sensor to receive commands and converts that to switch actions",
    category: "",
    iconUrl: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
	iconX2Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    iconX3Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    )

preferences {
	section("") {
        paragraph " V1.0.0 "
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
                  title: "Water Sensor to Switch",
                  required: false,
                  "Uses a water sensor to receive commands and converts that to switch actions"
         }         

 	section("Switch to enable/disable app"){
		input "enableswitch1",  "capability.switch", title: "SmartApp Control Switch - Optional", multiple: true, required: false
}
	section("") {
		input "alarm", "capability.waterSensor", title: "Water Sensor", required: true
        input "actionType1", "bool", title: "Select Water Sensor action type: \r\n \r\n On = 'Switch' ON when 'DRY'  \r\n Off = 'Switch' ON when 'WET' ", required: true, defaultValue: false
	}
	 section("Turn on/off this switch when wet/dry"){
		input "switch1",  "capability.switch", title: "Switch to control", multiple: true, required: false
	    input "msgDelay", "number", title: "Delay between actions (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
     }
    section("Logging"){
            input "debugMode", "bool", title: "Enable logging", required: true, defaultValue: false
        }
	}


def installed() {
	initialise()
   
}

def updated() {
	unsubscribe()
	initialise()
   
}

def initialise() {
log.debug "Initialised with settings: ${settings}"
 setAppVersion()
 logCheck()
 state.enable1 == 'on'
 state.timer1 == true
	subscribe(alarm, "water", waterHandler)
	subscribe(enableswitch1, "switch", enableswitchHandler)

}
  
def enableswitchHandler(evt) {
state.enable1 = evt.value
LOGDEBUG(" Control switch event = $state.enable1")
} 
  
  
def waterHandler(evt) {
// state.timer1 = true // debug only
// state.enable1 = 'on' // debug only


	state.control1 = evt.value
LOGDEBUG("Handler event = $state.control1 ************************************************")
controlType1()
LOGDEBUG("Checked")
            
// goes wet
if (state.enable1 == 'on' && state.control1 == "wet" && state.type1 == 'off' && state.timer1 == true) { 
	turnOff()
	
}
else if (state.enable1 == 'on' && state.control1 == "wet" && state.type1 == 'on' && state.timer1 == true) { 
	turnOn()
		
}

// goes dry

else if (state.enable1 != 'off' && state.control1 == "dry" && state.type1 == 'off' && state.timer1 == true) {  
	turnOn()
		
}
else if (state.enable1 != 'off' && state.control1 == "dry" && state.type1 == 'on' && state.timer1 == true) { 
	turnOff()
		
}
else if (state.enable1 == 'off'){
LOGDEBUG("Control switch is OFF - Unable to proceed")
}
}



def turnOn(){
LOGDEBUG("Turning on...")
		switch1.on()
		startTimer()
}



def turnOff(){
LOGDEBUG("turning off...")
		switch1.off()
		startTimer()
}

def startTimer(){
LOGDEBUG("Starting timer...")
state.timer1 = false
state.timeDelay =  msgDelay
LOGDEBUG(" state.timeDelay =  $state.timeDelay seconds")
LOGDEBUG("Waiting for $msgDelay seconds before resetting timer to allow further actions")
runIn(state.timeDelay, resetTimer)
}

def resetTimer() {
state.timer1 = true
LOGDEBUG( "Timer reset - Actions allowed")
}




 
 // check action type switch

 def controlType1(){
 LOGDEBUG("Checking control type...")
	if (actionType1 == true){ 
    state.type1 = 'on'
    LOGDEBUG( "Switch Type: ON (Switch when dry)" )}
    else if (actionType1 == false){ 
    state.type1 = 'off'
     LOGDEBUG( "Switch Type: OFF (Switch when wet)") }
     }
     
     
     
     // Logging & App version...
     
// define debug action
def logCheck(){
state.checkLog = debugMode
if(state.checkLog == true){
log.info "All Logging Enabled"
}
else if(state.checkLog == false){
log.info "Further Logging Disabled"
}

}
def LOGDEBUG(txt){
    try {
    	if (settings.debugMode) { log.debug("${app.label.replace(" ","_").toUpperCase()}  (AppVersion: ${state.appversion}) - ${txt}") }
    } catch(ex) {
    	log.error("LOGDEBUG unable to output requested data!")
    }
}


def setAppVersion(){
    state.appversion = "1.0.0"
}