/**
 *  ****************  Temperature Controlled Switch  ****************
 *
 *	Credits: 
 *	Parts of 'allOk()' code come from an app by: TIM SLAGLE
 *	Parts of 'LOGDEBUG()' code from an app by: BRIAN LOWRANCE
 *
 *  Design Usage:
 *  This was designed to.... 
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
 *  Created: 18/09/2017
 *  Last Update:
 *
 *  Changes:
 *
 *
 *  V2.0 - Recode, debug & added time restrictions
 *  V1.2 - Added action to turn off heating if 'allow' switch turned off
 *  V1.1 - Added days of the week
 *  V1.0 - POC
 *
 *  Author: Cobra
 */



definition(
    name: "Temperature Controlled Switch V2",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "This SmartApp was designed to control a heater - turning on/off with  varying temperatures. \r\nIt has an optional 'override' switch and configurable restrictions on when it can run",
    category: "",
    iconUrl: "http://54.246.165.27/img/icons/temp.png",
	iconX2Url: "http://54.246.165.27/img/icons/temp.png",
    iconX3Url: "http://54.246.165.27/img/icons/temp.png",
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
    
    
// Subscriptions ********************************************************************
    subscribe(switch1, "switch", switchEnableNow) // Default - Enable/Disable switch
// **********************************************************************************
// Others...
   subscribe(temperatureSensor1, "temperature", temperatureHandler)
 
    
}




// main page *************************************************************************
def introPage() {
    dynamicPage(name: "introPage") {
   
    
    
        section() {       
        paragraph image: "http://54.246.165.27/img/icons/temp.png",
                  title: "Temperature Controlled Switch",
                  required: false,
                 "This SmartApp was designed to control a heater - turning on/off with  varying temperatures. \r\nIt has an optional 'override' switch and configurable restrictions on when it can run"
                  }
                  
        section() {   
        paragraph image: "http://54.246.165.27/img/icons/cobra3.png",
                         "Version: $state.appversion - Brought to you by Cobra"
   				  }      
    
    
		section("Basic App Settings") {
	input "switch1", "capability.switch", title: "Select switch to enable/disable app (Optional)", required: false, multiple: false 
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
     section("Select Temperature Sensor") {
		input "temperatureSensor1", "capability.temperatureMeasurement" , required: true
	}
	section("Desired Temperature") {
		input "temperature1", "number", title: "Temperature?", required: true
	}
   	section("Control this switch/heater...") {
		input "switch2", "capability.switch", required: true
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

def temperatureHandler(evt) {
	if(allOk){
    
LOGDEBUG("All ok so can continue...")


	def myTemp = temperature1
	// Is reported temp below or above setting?	
	if (evt.doubleValue < myTemp) {
		
		LOGDEBUG( "Reported temperature is below $myTemp so activating $switch2")
			switch2.on()
		}
        else if (evt.doubleValue >= myTemp) {
       LOGDEBUG( "Reported temperature is equal to, or above, $myTemp so deactivating $switch2")
			switch2.off()
	}





}
	else if(!allOk){
LOGDEBUG(" Not ok - one or more conditions are not met")
LOGDEBUG("modeOk = $modeOk - daysOk = $daysOk - timeOk = $timeOk - enableOk = $enableOk")
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
	modeOk && daysOk && timeOk && enableOk
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
if(state.enable == 'on'){
result = true }

else if(state.enable == 'off'){
result = false }

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
    state.appversion = "2.0.0"
}