/**
 *  ****************  Temperature Controlled Switch  ****************
 *
 *	Credits: 
 *	Parts of 'allOk()' code come from an app by: TIM SLAGLE
 *	Parts of 'LOGDEBUG()' code from an app by: BRIAN LOWRANCE
 *
 *  Design Usage:
 *  This was designed to control a heater or cooler - Switching on/off around desired temperature
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
 *  Created: 28/01/2018
 *  Last Update:
 *
 *  Changes:
 *
 *  V2.4.0 - Added variable external temp setting option (Using virtual controller DTH)
 *  V2.3.0 - Added presence sensor restriction
 *  V2.2.1 - Debug installation issue where app would not install if a time was not set
 *  V2.2.0 - Added ability to use for cooling as well as heating
 *  V2.1.1 - Debug
 *	V2.1.0 - Added optional contact sensor to turn off heating if windows opens
 *  V2.0.0 - Recode, debug & added time restrictions
 *  V1.2.0 - Added action to turn off heating if 'allow' switch turned off
 *  V1.1.0 - Added days of the week
 *  V1.0.0 - POC
 *
 *  Author: Cobra
 */



definition(
    name: "Temperature Controlled Switch",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "This SmartApp was designed to control a heater - turning on/off with  varying temperatures. \r\nIt has an optional 'override' switch and configurable restrictions on when it can run \r\nYou can also use an optional external 'Temperature Controller'",
    category: "",
    
    
    iconUrl: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/temp.png",
	iconX2Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/temp.png",
    iconX3Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/temp.png",
    )


preferences {

	page name: "introPage", title: "", install: false, uninstall: true, nextPage: "settingsPage"
    page name: "settingsPage", title: "", install: false, uninstall: true, nextPage: "inputPage"
    page name: "inputPage", title: "", install: false, uninstall: true, nextPage: "namePage"
    page name: "namePage", title: "", install: true, uninstall: true

}
          
          




def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	log.info "Initialised with settings: ${settings}"
	setAppVersion()
	LOGDEBUG("")
    logCheck()
    state.enable = "on"
    log.info "state.enable = $state.enable"
    getpresenceOk()
    
// Subscriptions ********************************************************************
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
    if(switch1){ subscribe(switch1, "switch", switchEnableNow) } // Default - Enable/Disable switch
    if(ending){	schedule(ending, offNow) }
    if(contact1){ subscribe(contact1, "contact", contactHandler)}
    if(restrictPresenceSensor) {subscribe (restrictPresenceSensor, "presence", presenceHandler)}
    if(temperatureControl1) {subscribe (temperatureControl1, "temperature", customTempHandler)}
   
}


// main page *************************************************************************
def introPage() {
    dynamicPage(name: "introPage") {
   
    
    
        section() {       
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/temp.png",
                  title: "Temperature Controlled Switch",
                  required: false,
                 "This SmartApp was designed to control a heater or cooler - turning on/off with  varying temperatures. \r\nIt has optional, configurable restrictions on when it can run. \r\nIt also has an optional 'Temperature Control Switch' to control temperature setting."
                  }
                  
        section() {   
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
                         "Version: $state.appversion  - Copyright © 2017 Cobra"
   				  }      
    
    
		section("Basic App Settings") {
        input "appmode", "bool", title: " Select mode of operation\r\n Off = Heating - On = Cooling", required: true, submitOnChange: true, defaultValue: false    
		input "switch1", "capability.switch", title: "Select switch to enable/disable app (Optional)", required: false, multiple: false 
        }
        section("Only allow action if this sensor is 'Present'") {
        input "restrictPresenceSensor", "capability.presenceSensor", title: "Select presence sensor (Optional)", required: false, multiple: false
    }  
}

}

// Settings Page ***************************************************
def settingsPage(){
	 dynamicPage(name: "settingsPage") {
     
     
 // BASIC SETTINGSS

		
    section("RunTime Settings") {
    input "starting", "time", title: "Start Time (Optional)", required: false
    input "ending", "time", title: "End Time (Optional)", required: false
    input "days", "enum", title: "Select Days of the Week (Optional)", required: false, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
	
     
	}     
       
 }    
}

// Input Page  *********************************************************************
def inputPage(){
	 dynamicPage(name: "inputPage") {
     section() {
		input "temperatureSensor1", "capability.temperatureMeasurement" , title: "Select Temperature Sensor", required: true
	}
    
    
    
    
	section("Desired Temperature") {
    
    input "tempMode", "bool", title: " Select mode of operation\r\n Off = Fixed Temperature - On = Variable Temperature", required: true, submitOnChange: true, defaultValue: false  
    if(tempMode == true){
    input "temperatureControl1", "capability.temperatureMeasurement" , title: "Select Controller", required: true
    
    }
   
    else if(tempMode == false){
		input "temperature1", "number", title: "Temperature?", required: true
	}
   }
   	section("Control this Switch/Heater/Cooler...") {
		input "switch2", "capability.switch", required: true, multiple: true
	}
    section("Switch off if this contact is open (Optional)") {
		input "contact1", "capability.contactSensor", required: false, multiple: true
	}
     
 }
}

// NamePage ***************************************************
def namePage() {
       dynamicPage(name: "namePage") {
       
            section("App name") {
                label title: "Enter a name for this app (Optional)", required: false
            }
             section("Modes") {
           		mode title: "Set for specific mode(s) (Optional)", required: false
            }
             section("Logging") {
            input "debugMode", "bool", title: "Debug Logging (Optional)", required: true, defaultValue: false
  	        }
      }  
    }



// Handlers & Actions *****************************

def customTempHandler(evt){
state.reqTemp = evt.doubleValue
LOGDEBUG("Required Temp set to: $state.reqTemp degrees by virtual controller: $temperatureControl1 ")
runTemp()
}



def presenceHandler(evt){
state.presence1Now = evt.value
if (state.presence1Now == 'not present'){
	LOGDEBUG("Presence is $state.presence1Now - Switching off now...")
switch2.off()
	LOGDEBUG("$switch2 is OFF - Heating/Cooling Disabled")
	}
 else if (state.presence1Now == 'present'){
LOGDEBUG("Presence is $state.presence1Now - Heating/Cooling Allowed")
switch2.on()
	}
}



def contactHandler(evt){
	state.contact1Now = evt.value

if (state.contact1Now == 'open'){
	LOGDEBUG("Contact is $state.contact1Now - Switching off now...")
switch2.off()
	LOGDEBUG("$switch2 is OFF - Heating/Cooling Disabled")
	}
 else{
LOGDEBUG("Contact is $state.contact1Now - Heating/Cooling Allowed")
	}
}

def offNow(){
LOGDEBUG("Time expired.. Switching off now...")
switch2.off()
}



def temperatureHandler(evt) {
	state.newTemp = evt.doubleValue
LOGDEBUG("requested temperature = $state.newTemp degrees")
	runTemp()
}

def runTemp(){
	def currTemp = state.newTemp
LOGDEBUG("Reported temperature is now: $currTemp degrees.")

	if(allOk){    
LOGDEBUG("All ok so can continue...")

	if(appmode == false){
	LOGDEBUG("Configured for heating mode")
    
	if(tempMode == true){ 
     state.confTemp = state.reqTemp
     LOGDEBUG("Configured to use variable temp controller - state.confTemp = $state.confTemp")
	}  
    else if(tempMode == false){
    state.confTemp = temperature1
    LOGDEBUG("Configured to use fixed temp - state.confTemp = $state.confTemp")
    }
    
	// Is reported temp below required setting?	
    if (currTemp < state.confTemp) {
	LOGDEBUG( "Reported temperature is below $state.confTemp so activating $switch2")
			switch2.on()
	}
    else if (currTemp >= state.confTemp) {
    LOGDEBUG( "Reported temperature is equal to, or above, $state.confTemp so deactivating $switch2")
			switch2.off()
	}
}

else if(appmode == true){
LOGDEBUG("Configured for cooling mode")

    if(tempMode == true){   
	state.confTemp = state.reqTemp
     LOGDEBUG("Configured to use variable temp controller - state.confTemp = $state.confTemp")
    }
    else if(tempMode == false){
    state.confTemp = temperature1
    LOGDEBUG("Configured to use fixed temp - state.confTemp = $state.confTemp")
    }

	// Is reported temp above setting?	
	if (currTemp > state.confTemp) {	
	LOGDEBUG( "Reported temperature is above $myTemp so activating $switch2")
			switch2.on()
	}
    else if (currTemp <= state.confTemp) {
    LOGDEBUG( "Reported temperature is equal to, or below, $myTemp so deactivating $switch2")
			switch2.off()
	}
  }
}
	else if(!allOk){
LOGDEBUG(" Not ok - one or more conditions are not met")
LOGDEBUG("modeOk = $modeOk - daysOk = $daysOk - timeOk = $timeOk - enableOk = $enableOk - presenceOK = $presenceOk")
	}
}


// Check if ok to run *** (Time, Mode, Day & Enable Switch) ************************************

// disable/enable switch
def switchEnableNow(evt){
	state.enable = evt.value
	LOGDEBUG( "Enable/Disable switch $switch1 is $state.currS1")
	if (state.enable == "off" ) {
		switch2.off()
   }
}


private getAllOk() {
	modeOk && daysOk && timeOk && enableOk && contactOk && presenceOk
}


private getpresenceOk() {
	def result = true
		if (state.presence1Now == 'not present' ) {
	result = false
	}
    else  {
	result = true
    }
    LOGDEBUG("presenceOk = $result")
	result
}




private getcontactOk() {
	def result = true
		if (state.contact1Now != 'open' ) {
	result = true
	}
    else if (state.contact1Now == 'open' ) {
	result = false
    }
    LOGDEBUG("contactOk = $result")
	result
}


private getModeOk() {
	def result = !modes || modes.contains(location.mode)
	LOGDEBUG("modeOk = $result")
	result
}

private getDaysOk() {
	def result = true
	if (days) {
		def df = new java.text.SimpleDateFormat("EEEE")
		if (location.timeZone) {
			df.setTimeZone(location.timeZone)
		}
		else {
			df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
		}
		def day = df.format(new Date())
		result = days.contains(day)
	}
	LOGDEBUG("daysOk = $result")
	result
}

private getTimeOk() {
	def result = true
	if (starting && ending) {
		def currTime = now()
		def start = timeToday(starting).time
		def stop = timeToday(ending).time
		result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
	}
	LOGDEBUG("timeOk = $result")
	result
}

private hhmm(time, fmt = "h:mm a"){
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}

private getTimeIntervalLabel(){
	(starting && ending) ? hhmm(starting) + "-" + hhmm(ending, "h:mm a z") : ""
}


private getenableOk(){
	def result = true
	if(state.enable == 'on'){result = true }
	else if(state.enable == 'off'){result = false }
	LOGDEBUG("enableOk = $result")
	result
}



private hideOptionsSection() {
	(starting || ending || days || modes) ? false : true
}

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
    	if (settings.debugMode) { log.debug("${app.label.replace(" ",".").toUpperCase()}  (AppVersion: ${state.appversion})  ${txt}") }
    } catch(ex) {
    	log.error("LOGDEBUG unable to output requested data!")
    }
}




// App Version   *********************************************************************************
def setAppVersion(){
    state.appversion = "2.4.0"
}