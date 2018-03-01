/**
 *  ****************  Fibaro Colour Changer..  ****************
 *
 *  Design Usage:
 *  This is the 'Parent' app for temperature controlled fibaro..
 *
 *
 *  Copyright 2017 Andrew Parker.
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
 *  Last Update: 01/03/2018
 *
 *  Changes:
 *
 *  V1.0.1 - Debug
 *  V1.0.0 - POC
 *
 */

 
 definition(
    name: "Fibaro Temperature Colour Changer",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Parent App for Fibaro RGBW control via temperature.",
   category: "Fun & Social",
    iconUrl: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    iconX2Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    iconX3Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png")

 preferences {
    
    page name: "mainPage", title: "", install: false, uninstall: true, nextPage: "actionPage"
    page name: "actionPage", title: "", install: false, uninstall: true, nextPage: "finalPage"
    page name: "finalPage", title: "", install: true, uninstall: true
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
	logCheck()
    log.debug "there are ${childApps.size()} child smartapps"
    childApps.each {child ->
        log.debug "child app: ${child.label}"
    }
    state.override = false
    state.enableSwitch = 'on'
   
	startUp()
    setAppVersion()
    
    checkTime()
    
    if(!setRise){state.riseSetGo = true}
    
  // subscriptions  
    if(switch1){ subscribe(switch1, "switch", switchEnableNow) } 
    if(switch2){ subscribe(switch2, "switch", switchOverNow) } 
	subscribe(location, "sunriseTime", sunriseSunsetTimeHandler)
	subscribe(location, "sunsetTime", sunriseSunsetTimeHandler)
}



// main page *************************************************************************
def mainPage() {
    dynamicPage(name: "mainPage") {
      
        section("") {
        
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
                  title: "Fibaro Bright/Dim SwitchOver - Temperature Controlled",
                  required: false,
                  " Control a Fibaro RGBW Controller's colours Dependant upton temperature\r\n" +
                  " This is the Parent"
                  
    }
     section() {
     paragraph "Version: $state.appversion - Copyright © 2018 Cobra"
       
                        
    }   
    
     section {
            app(name: "switchTempChild", appName: "Fibaro_Temperature_RGBW_Child", namespace: "Cobra", title: "Create New Temperature Child", multiple: true)
           
            }
            

  }
}


// action page *************************************************************************
def actionPage() {
    dynamicPage(name: "actionPage") {

// inputs
 
 	section(){
		input "fibaro1", "capability.switch", title: "Fibaro Controller", required: true, multiple: true
        }
 
 	section(){
		input "switch1", "capability.switch", title: "Optional On/Off switch", required: false
		}
         
    section("Optional Colour Switch"){
		input "switch2", "capability.switch", title: "Optional Dim/Bright switch (Off=Dim (Temperature Child Controlled) - On=Bright - Custom Colour)", required: false, submitOnChange: true
        if(switch2){
        input "redDimB", "number", title: "How much RED? (0-100)", defaultValue: "0", multiple: false, required: false
        input "greenDimB", "number", title: "How much GREEN? (0-100)", defaultValue: "0", multiple: false, required: false
        input "blueDimB", "number", title: "How much BLUE? (0-100)", defaultValue: "0", multiple: false, required: false
        input "whiteDimB", "number", title: "How much White? (0-100)", defaultValue: "0", multiple: false, required: false
          } 
		}
		
        
	}
}




// name page *************************************************************************
def finalPage() {
       dynamicPage(name: "finalPage") {
       
            section("Parent Name") {
                label title: "Enter a name for this parent app", required: false
            }
             section("Modes") {
           		mode title: "Only allow actions when in specific mode(s)", required: false
                }
             section("Sunset & Sunrise") {                  
				input "setRise", "bool", title: "Only allow actions between SunSet and SunRise", required: false, submitOnChange: true, defaultValue: false
				
				if(setRise){
				input "sunriseOffsetValue", "number", title: "Optional Sunrise Offset (Minutes)", required: false
				input "sunriseOffsetDir", "enum", title: "Before or After", required: false, options: ["Before","After"]
       			input "sunsetOffsetValue", "number", title: "Optional Sunset Offset (Minutes)", required: false
				input "sunsetOffsetDir", "enum", title: "Before or After", required: false, options: ["Before","After"]
        }
      }
        section("Time Restrictions") {  
      input "restrictions1", "bool", title: "Restrict by Time", required: false, defaultValue: false, submitOnChange: true
      
           	if(restrictions1){       		
   				 input "fromTime", "time", title: "Allow actions from", required: false
   				 input "toTime", "time", title: "Allow actions until", required: false
  //  			 input "days", "enum", title: "Select Days of the Week", required: false, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
    	}
      }
      		section("Logging"){
            	input "debugMode", "bool", title: "Enable logging", required: false, defaultValue: false
        }   
        
    }
}


//  Handlers


def switchEnableNow(evt){
state.enableSwitch = evt.value
if (state.enableSwitch == 'off') {switchoff()}
else if (state.enableSwitch == 'on') {setDimColour()}
}

//

def switchOverNow(evt){
state.switchOver = evt.value
if (state.switchOver == 'on'){
state.override = true
setBrightColour()
}
if (state.switchOver == 'off'){
state.override = false
setDimColour()
	}
}


// 
// Set Fibaro colours to level set by child app

def setTempColour(tempLevel1, redDim1, blueDim1, greenDim1, whiteDim1){
LOGDEBUG( " Receiving colour settings from child")

state.tempLevel = tempLevel1
state.redDim =  redDim1
state.blueDim = blueDim1
state.greenDim = greenDim1
state.whiteDim = whiteDim1

if (state.enableSwitch == 'off'){ switchoff()}
if(state.override == false){setDimColour()}

}
 
 // DIM
def setDimColour(){
checkTime()
if (state.enableSwitch != 'off' && state.riseSetGo == true && state.timeOK == true){
	LOGDEBUG( "Temperature is $state.tempLevel degrees - Turning $fibaro1 to 'Dim' custom colour' as prescribed by child app ")
		fibaro1.setLevelRed(state.redDim)
		fibaro1.setLevelBlue(state.blueDim)
		fibaro1.setLevelGreen(state.greenDim)
        fibaro1.setLevelWhite(state.whiteDim)
		}
}



// BRIGHT
def setBrightColour(){
checkTime()
if (state.enableSwitch != 'off' && state.riseSetGo == true && state.timeOK == true){
LOGDEBUG( "Setting 'Bright' colour on now!")
fibaro1.setLevelRed(redDimB)
fibaro1.setLevelBlue(blueDimB)
fibaro1.setLevelGreen(greenDimB)
fibaro1.setLevelWhite(whiteDimB)

	}

}

def switchoff(){
LOGDEBUG( "Turning off $fibaro1")
    	fibaro1.setLevelRed(0)
		fibaro1.setLevelBlue(0)
		fibaro1.setLevelGreen(0)
		fibaro1.setLevelWhite(0)
		fibaro1.off()
 }
 
 
 def startUp(){
 LOGDEBUG( "setting start default for $fibaro1 (until temp change)")
	state.redDim =  "2"
	state.blueDim = "2"
	state.greenDim = "2"
	state.whiteDim = "2"
        }
        
        
// Check time allowed to run...

def checkTime(){
def timecheckNow = fromTime
if (timecheckNow != null){
def between = timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)
    if (between) {
    state.timeOK = true
   LOGDEBUG("Time is ok so can continue...")
    
}
else if (!between) {
state.timeOK = false
LOGDEBUG("Time is NOT ok so cannot continue...")
	}
  }
else if (timecheckNow == null){  
state.timeOK = true
  LOGDEBUG("Time restrictions have not been configured -  Continue...")
  }
}







// Sunset & Sunrise Handlers ====================================================
def sunriseSunsetTimeHandler(evt) {
	LOGDEBUG("sunriseSunsetTimeHandler()")
	astroCheck()
}

def astroCheck() {
LOGDEBUG("Calling astroCheck...")
def astroGo = setRise
if (astroGo == true){

	def s = getSunriseAndSunset(sunriseOffset: sunriseOffset, sunsetOffset: sunsetOffset)
	def now = new Date()
	def riseTime = s.sunrise
	def setTime = s.sunset
	LOGDEBUG("riseTime: $riseTime")
	LOGDEBUG("setTime: $setTime")

	if (state.riseTime != riseTime.time) {
		unschedule("sunriseHandler")

		if(riseTime.before(now)) {
			riseTime = riseTime.next()
		}

		state.riseTime = riseTime.time

		LOGDEBUG("Scheduling sunrise handler for $riseTime")
		schedule(riseTime, sunriseHandler)
	}

	if (state.setTime != setTime.time) {
		unschedule("sunsetHandler")

	    if(setTime.before(now)) {
		    setTime = setTime.next()
	    }

		state.setTime = setTime.time

		LOGDEBUG("Scheduling sunset handler for $setTime")
	    schedule(setTime, sunsetHandler)
	}
  }
   LOGDEBUG("astroCheck - state.riseSetGo = $state.riseSetGo")
}


def sunsetHandler() {
LOGDEBUG("Sun has set!")
  def riseCheck = setRise

	if (riseCheck == true){
	state.riseSetGo = true
    LOGDEBUG("state.riseSetGo = $state.riseSetGo")
	LOGDEBUG("sunsetHandler - Actions Allowed")
    setDimColour()
   
	}
}


def sunriseHandler() {
LOGDEBUG("Sun has risen!")
  def riseCheck = setRise

	if (riseCheck == true){
	state.riseSetGo = false
    LOGDEBUG("state.riseSetGo = $state.riseSetGo")
	LOGDEBUG("sunriseHandler - Actions NOT Allowed")
    switchoff()
	}
}

private getSunriseOffset() {
	sunriseOffsetValue ? (sunriseOffsetDir == "Before" ? "-$sunriseOffsetValue" : sunriseOffsetValue) : null
}

private getSunsetOffset() {
	sunsetOffsetValue ? (sunsetOffsetDir == "Before" ? "-$sunsetOffsetValue" : sunsetOffsetValue) : null
}
// End Sunset & Sunrise Handlers ====================================================

// Define debug action  *****************************************
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
    	if (settings.debugMode) { log.debug("${app.label.replace(" ","_").toUpperCase()}  (App Version: ${state.appversion}) - ${txt}") }
    } catch(ex) {
    	log.error("LOGDEBUG unable to output requested data!")
    }
}
// end debug action ********************************************



// App Version   ***********************************************
def setAppVersion(){
    state.appversion = "1.0.1"
}
// end app version *********************************************
