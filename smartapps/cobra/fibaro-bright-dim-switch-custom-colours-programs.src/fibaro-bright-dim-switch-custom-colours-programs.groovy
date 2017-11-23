/**
 *  ****************  Fibaro Bright/Dim Switch ****************
 *
 *  Design Usage:
 *  This was designed to be used with the excellent Fibaro RGBW DTH by: '@codersaur' (David Lomas)
 *  Control a Fibaro RGBW Controller's colours or programs (switching between two) with a switch
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
 *  You should also give a 'shout out' to @codersaur, as without his DTH, this app would not be possible
 *
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
 *  Last Update: 23/11/2017
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
    name: "Fibaro Bright/Dim Switch - Custom Colours/Programs",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Control a Fibaro RGBW Controller's colours or programs (switching between two) with a switch",
    category: "My Apps",
    iconUrl: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
	iconX2Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    iconX3Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
)


 preferences {
    
    page name: "mainPage", title: "", install: false, uninstall: true, nextPage: "actionPage"
    page name: "actionPage", title: "", install: false, uninstall: true, nextPage: "finalPage"
    page name: "finalPage", title: "", install: true, uninstall: true
}

def installed()
{
	initialise()
}

def updated()
{
	unsubscribe()
	initialise()
}


def initialise(){
log.debug "Initialised with settings: ${settings}"


// setup defaults
		setAppVersion()
		logCheck()
		state.riseSetGo = true
        state.appGo = true
		astroCheck()
        LOGDEBUG("state.riseSetGo = $state.riseSetGo")
         LOGDEBUG("state.appGo = $state.appGo")

// Basic Subscriptions    

		
		subscribe(location, "sunriseTime", sunriseSunsetTimeHandler)
		subscribe(location, "sunsetTime", sunriseSunsetTimeHandler)
   		subscribe(switch1, "switch.on", switchOnHandler1)
        subscribe(switch1, "switch.off", switchOffHandler1)
        subscribe(switch2, "switch.on", switchOnHandler2)
        subscribe(switch2, "switch.off", switchOffHandler2)

        
        
}

// main page *************************************************************************
def mainPage() {
    dynamicPage(name: "mainPage") {
      
        section("") {
        
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
                  title: "Fibaro Bright/Dim SwitchOver - Custom Colours/Programs",
                  required: false,
                  " Control a Fibaro RGBW Controller's colours or programs (switching between two) with a switch \r\n" +
                  " Note: Dim levels cannot be set on Fibaro's set programs"
                  
    }
     section() {
     paragraph "Version: $state.appversion - Copyright © 2017 Cobra"
       
                        
    }             
      section(){
		input "switch1", "capability.switch",  required: false, title: "App enable/disable switch (Optional)"
	}
    section(){
		input "switch2", "capability.switch",  required: true, title: "Dim/Bright switch"
	}
	section(){
		input "fibaro1", "capability.switch", title: "Fibaro Controller", required: true, multiple: true
        }
      
  }
}

// action page *************************************************************************
def actionPage() {
    dynamicPage(name: "actionPage") {

 section() {
 
		lightInputs()
		
        
	}
}
}

// name page *************************************************************************
def finalPage() {
       dynamicPage(name: "finalPage") {
       
            section("Automation name") {
                label title: "Enter a name for this message automation", required: false
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
      
      		section("Logging"){
            	input "debugMode", "bool", title: "Enable logging", required: true, defaultValue: false
        }   
        
    }
}









def lightInputs() {
		input "setColProg1", "enum", title: "'Dim' - Colour or Program?",required: true, submitOnChange: true, options: ["Custom Colour", "Fireplace", "Storm", "Deepfade","Litefade", "Police", "WarmWhite", "ColdWhite", ]
  
  if (setColProg1) {
    state.selection1 = setColProg1

		if(state.selection1 == "Custom Colour"){

		input "redDim1", "number", title: "How much RED? (0-100)", defaultValue: "0", multiple: false, required: false
        input "greenDim1", "number", title: "How much GREEN? (0-100)", defaultValue: "0", multiple: false, required: false
        input "blueDim1", "number", title: "How much BLUE? (0-100)", defaultValue: "0", multiple: false, required: false
        input "whiteDim1", "number", title: "How much White? (0-100)", defaultValue: "0", multiple: false, required: false
		}       
	}

		input "setColProg2", "enum", title: "'Bright' - Colour or Program?",required: true, submitOnChange: true, options: ["Custom Colour", "Fireplace", "Storm", "Deepfade","Litefade", "Police", "WarmWhite", "ColdWhite", ]

 if (setColProg2) {
    state.selection2 = setColProg2
		
        if(state.selection2 == "Custom Colour"){

 		input "redDim2", "number", title: "How much RED? (0-100)", defaultValue: "0", multiple: false, required: false
        input "greenDim2", "number", title: "How much GREEN? (0-100)", defaultValue: "0", multiple: false, required: false
        input "blueDim2", "number", title: "How much BLUE? (0-100)", defaultValue: "0", multiple: false, required: false
        input "whiteDim2", "number", title: "How much White? (0-100)", defaultValue: "0", multiple: false, required: false
		}
	}
}






def switchOnHandler1(evt) {
LOGDEBUG("$evt.value: $evt, $settings")
state.appGo = true
setDim()
}
	

def switchOffHandler1(evt) {
state.appGo = false
LOGDEBUG("$evt.value: $evt, $settings")
	allOff()
}

def switchOnHandler2(evt) {
if(state.riseSetGo == true && state.appGo == true){
fibaro1.off()
	setBright()
    }
    
else if(state.appGo == false){
LOGDEBUG("Unable to continue - Disabled by switch")
}    
else if(state.riseSetGo == false){
LOGDEBUG("Unable to continue - Sunset/Sunrise restriction")
}

}

def switchOffHandler2(evt) {
if(state.riseSetGo == true && state.appGo == true){
fibaro1.off()
	setDim()
    }
else if(state.appGo == false){
LOGDEBUG("Unable to continue - Disabled by switch")
}    
else if(state.riseSetGo == false){
LOGDEBUG("Unable to continue - Sunset/Sunrise restriction")
}

}

def allOff(){
	LOGDEBUG( "Turning off $fibaro1")
    	fibaro1.setLevelRed(0)
		fibaro1.setLevelBlue(0)
		fibaro1.setLevelGreen(0)
		fibaro1.setLevelWhite(0)
		fibaro1.off()

}

// Configure custom colour 1
def picker1(){

LOGDEBUG( "DIM - Setting $fibaro1 to 'Custom Colour' ")
def customRedLevel1 = redDim1 as int
def customBlueLevel1 = blueDim1 as int
def customGreenLevel1 = greenDim1 as int
def customWhiteLevel1 = whiteDim1 as int


fibaro1.setLevelRed(customRedLevel1)
fibaro1.setLevelBlue(customBlueLevel1)
fibaro1.setLevelGreen(customGreenLevel1)
fibaro1.setLevelWhite(customWhiteLevel1)
}


// Configure custom colour 2
def picker2(){

LOGDEBUG("BRIGHT - Setting $fibaro1 to 'Custom Colour' ")
def customRedLevel2 = redDim2 as int
def customBlueLevel2 = blueDim2 as int
def customGreenLevel2 = greenDim2 as int
def customWhiteLevel2 = whiteDim2 as int


fibaro1.setLevelRed(customRedLevel2)
fibaro1.setLevelBlue(customBlueLevel2)
fibaro1.setLevelGreen(customGreenLevel2)
fibaro1.setLevelWhite(customWhiteLevel2)
}



// set DIM colours/programs

def setDim(){
LOGDEBUG("Calling setDim...")
if(state.selection1 == "Custom Colour"){
LOGDEBUG("DIM - Setting Custom Colour to: ON")
	picker1()
	}
if(state.selection1 == "Fireplace"){
LOGDEBUG("DIM - Setting Fireplace to: ON")
	fibaro1.startFireplace()
	}  
if(state.selection1 == "Storm"){
LOGDEBUG("DIM - Setting Storm to: ON")
	fibaro1.startStorm()
	}     
if(state.selection1 == "Deepfade"){
LOGDEBUG("DIM - Setting Deepfade to: ON")
	fibaro1.startDeepFade()
	}   
if(state.selection1 == "Litefade"){
LOGDEBUG("DIM - Setting Lightfade to: ON")
	fibaro1.startLiteFade()
	} 
if(state.selection1 == "Police"){
LOGDEBUG("DIM - Setting Police: ON")
	fibaro1.startPolice()
	}
if(state.selection1 == "WarmWhite"){
LOGDEBUG("DIM - Setting Warmwhite: ON")
	fibaro1.warmWhite()
	}
if(state.selection1 == "ColdWhite"){
LOGDEBUG("DIM - Setting Coldwhite: ON")
	fibaro1.coldWhite()
	}    
    
}


// set BRIGHT colours/programs
def setBright(){
LOGDEBUG("Calling setBright...")
if(state.selection2 == "Custom Colour"){
LOGDEBUG("BRIGHT - Setting Custom Colour to: ON")
	picker2()
	}
if(state.selection2 == "Fireplace"){
LOGDEBUG("BRIGHT - Setting Fireplace to: ON")
	fibaro1.startFireplace()
	}  
if(state.selection2 == "Storm"){
LOGDEBUG("BRIGHT - Setting Storm to: ON")
	fibaro1.startStorm()
	}     
if(state.selection2 == "Deepfade"){
LOGDEBUG("BRIGHT - Setting Deepfade to: ON")
	fibaro1.startDeepFade()
	}   
if(state.selection2 == "Litefade"){
LOGDEBUG("BRIGHT - Setting Litefade to: ON")
	fibaro1.startLiteFade()
	} 
if(state.selection2 == "Police"){
LOGDEBUG("BRIGHT - Setting Police to: ON")
	fibaro1.startPolice()
	}
if(state.selection2 == "WarmWhite"){
LOGDEBUG("BRIGHT - Setting Warmwhite to: ON")
	fibaro1.warmWhite()
	}
if(state.selection2 == "ColdWhite"){
LOGDEBUG("BRIGHT - Setting Coldwhite to: ON")
	fibaro1.coldWhite()
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
    setDim()
	}
}


def sunriseHandler() {
LOGDEBUG("Sun has risen!")
  def riseCheck = setRise

	if (riseCheck == true){
	state.riseSetGo = false
    LOGDEBUG("state.riseSetGo = $state.riseSetGo")
	LOGDEBUG("sunriseHandler - Actions NOT Allowed")
    allOff()
	}
}

private getSunriseOffset() {
	sunriseOffsetValue ? (sunriseOffsetDir == "Before" ? "-$sunriseOffsetValue" : sunriseOffsetValue) : null
}

private getSunsetOffset() {
	sunsetOffsetValue ? (sunsetOffsetDir == "Before" ? "-$sunsetOffsetValue" : sunsetOffsetValue) : null
}


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
    state.appversion = "1.0.0"
}
// end app version *********************************************
