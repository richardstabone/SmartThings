/**
 *  Missed Voice Alerts.
 *
 *  Design Usage:
 *  This is the 'Child' app for message automation...
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
 *
 *  Last Update: 14/02/2018
 *
 *  Changes:
 *
 *
 *
 *  V1.1.0 - added to Message Central as a childapp & revamped GUI
 *  V1.0.0 - POC
 */
definition(
    name: "Missed_Voice_Alerts",
    namespace: "Cobra",
    author: "Andrew Parker", 
    description: "Set times to check if a timed message has been missed while you were away. If you missed an announcement then remind you when you get home.",
    category: "",
    
	parent: "Cobra:Message Central",
    
    iconUrl: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/voice.png",
    iconX2Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/voice.png",
    iconX3Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/voice.png")


preferences {

	 page name: "mainPage", title: "", install: false, uninstall: true, nextPage: "namePage"
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
 logCheck()
	subscribe(missedPresenceSensor1, "presence", missedPresenceCheckNow) 
    if(runTime1){schedule(runTime1, checkTime1)}
    if(runTime2){schedule(runTime2, checkTime2)}
    if(runTime3){schedule(runTime3, checkTime3)}
    if(runTime4){schedule(runTime4, checkTime4)}
    if(runTime5){schedule(runTime5, checkTime5)}
    if(runTime6){schedule(runTime6, checkTime6)}
    
    
    
    
}

def mainPage() {
    dynamicPage(name: "mainPage") {
      
        section {
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/voice.png",
                  title: "Missed Voice Alerts",
                  required: false,
                  "This child app allows you to set times to check if a previous timed message has been missed while you were away. If you missed an announcement then this reminds you when you get home"
                  }
                  
         section() {
   
        paragraph image: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra.png",
                  //       required: false,
                  "Version: $state.appversion - Copyright © 2018 Cobra"
    }
                  
 		section() {
		allInputs()
        }
	}
}




def namePage() {
       dynamicPage(name: "namePage") {
       
     
            section("Automation name") {
                label title: "Enter a name for this message automation", required: false
            }
            section("Logging") {
            input "debugMode", "bool", title: "Enable logging", required: true, defaultValue: false
  	        }
      }  
    }





def allInputs(){

 		input "missedPresenceSensor1", "capability.presenceSensor", title: "Select presence sensor", required: true, multiple: false
        input "missedMsgDelay", "number", title: "Delay after arriving home before reminder message (minutes)", defaultValue: '0', description: "Minutes", required: true
        input "speaker", "capability.musicPlayer", title: "Choose speaker(s)", required: true, multiple: true
		input "volume1", "number", title: "Speaker volume", description: "0-100%", defaultValue: "80",  required: true  
        input "preMsg", "text", title: "Message to speak before list of missed messages", required: false, defaultValue: "Welcome home! ,,, I know that you've just arrived ,,, but while you were away ,,, you missed the following messages ,,,"
	


        input "runTime1", "time", title: "Time 1st message is due", required: false, submitOnChange: true
			if(runTime1){input "missedMessage1", "text", title: "Message to play when presence sensor arrives (if this event missed)",  required: true}  
            
        input "runTime2", "time", title: "Time 2nd message is due", required: false, submitOnChange: true
        	if(runTime2){input "missedMessage2", "text", title: "Message to play when presence sensor arrives (if this event missed)",  required: true}
            
        input "runTime3", "time", title: "Time 3rd message is due", required: false, submitOnChange: true
        	if(runTime3){input "missedMessage3", "text", title: "Message to play when presence sensor arrives (if this event missed)",  required: true}   
            
        input "runTime4", "time", title: "Time 4th message is due", required: false, submitOnChange: true
        	if(runTime4){input "missedMessage4", "text", title: "Message to play when presence sensor arrives (if this event missed)",  required: true}
            
        input "runTime5", "time", title: "Time 5th message is due", required: false, submitOnChange: true
        	if(runTime5){input "missedMessage5", "text", title: "Message to play when presence sensor arrives (if this event missed)",  required: true}
            
        input "runTime6", "time", title: "Time 6th message is due", required: false, submitOnChange: true
        	if(runTime6){input "missedMessage6", "text", title: "Message to play when presence sensor arrives (if this event missed)",  required: true}
        

}













def missedPresenceCheckNow(evt){

	state.missedPresencestatus1 = evt.value
LOGDEBUG( "state.missedPresencestatus1 = $evt.value")
checkTimeMissedNow()
	def	myMissedDelay = 60 * missedMsgDelay

	if(state.missedPresencestatus1 == "present" && state.missedEventAll == true){
   
LOGDEBUG( "Telling you about missed events in $missedMsgDelay minute(s) (If there are any and I haven't already told you about them)")
    
    runIn(myMissedDelay, speakMissedNow)
    
    }
if(state.missedPresencestatus1 == "present" && state.missedEventAll == false){
LOGDEBUG("No missed messages while you were away")
	}
}

// check times......
def checkTime1(evt){
 if(state.missedPresencestatus1 == 'not present'){
    state.missedEvent1 = true
    state.alreadyDone = false
LOGDEBUG( "Missed  1st time event")
}
if(state.missedPresencestatus1 == 'present'){
	state.missedEvent1 = false
LOGDEBUG( "1st event not missed")
	}
    
 
    
}

def checkTime2(evt){
 if(state.missedPresencestatus1 == 'not present'){
    state.missedEvent2 = true
    state.alreadyDone = false
LOGDEBUG( "Missed 2nd time event")
}
if(state.missedPresencestatus1 == 'present'){
	state.missedEvent2 = false
LOGDEBUG("2nd event not missed")
	}
    
   
    
}

def checkTime3(evt){
 if(state.missedPresencestatus1 == 'not present'){
    state.missedEvent3 = true
    state.alreadyDone = false
LOGDEBUG( "Missed 3rd time event")
}
if(state.missedPresencestatus1 == 'present'){
	state.missedEvent3 = false
LOGDEBUG("3rd event not missed")
	}
  
    
}

def checkTime4(evt){
 if(state.missedPresencestatus1 == 'not present'){
    state.missedEvent4 = true
    state.alreadyDone = false
LOGDEBUG("Missed 4th time event")
}
if(state.missedPresencestatus1 == 'present'){
	state.missedEvent4 = false
LOGDEBUG( "4th event not missed")
	}
    
   
    
}

def checkTime5(evt){
 if(state.missedPresencestatus1 == 'not present'){
    state.missedEvent5 = true
    state.alreadyDone = false
LOGDEBUG("Missed 5th time event")
}
if(state.missedPresencestatus1 == 'present'){
	state.missedEvent1 = false
LOGDEBUG( "5th event not missed")
	}
    

    
}

def checkTime6(evt){
 if(state.missedPresencestatus1 == 'not present'){
    state.missedEvent6 = true
    state.alreadyDone = false
LOGDEBUG( "Missed 6th time event")
}
if(state.missedPresencestatus1 == 'present'){
	state.missedEvent6 = false
LOGDEBUG("6th event not missed")
	}
    
 
    
}

def resetMissed(){
state.missedEvent1 = false
state.missedEvent2 = false
state.missedEvent3 = false
state.missedEvent4 = false
state.missedEvent5 = false
state.missedEvent6 = false


}




def checkTimeMissedNow(){
LOGDEBUG( "Checking missed events now...")
if (state.missedEvent1 == true || state.missedEvent2 == true || state.missedEvent3 == true || state.missedEvent4 == true || state.missedEvent5 == true || state.missedEvent6 == true){
state.missedEventAll = true
LOGDEBUG( "One or more messaged missed state.missedEventAll = $state.missedEventAll")

}

else {
LOGDEBUG( "No missed messages")
state.missedEventAll == false
	}
}


// saved for later use
def checkDoneAll(){
state.alreadyDoneAll = false
}
	



def speakMissedNow(){
LOGDEBUG("Speaking now...")
state.mmsg1 = ""
state.mmsg2 = ""
state.mmsg3 = ""
state.mmsg4 = ""
state.mmsg5 = ""
state.mmsg6 = ""

if(missedMessage1 != null && state.missedEvent1 == true){state.mmsg1 = missedMessage1}
if(missedMessage2 != null && state.missedEvent2 == true){state.mmsg2 = missedMessage2}
if(missedMessage3 != null && state.missedEvent3 == true){state.mmsg3 = missedMessage3}
if(missedMessage4 != null && state.missedEvent4 == true){state.mmsg4 = missedMessage4}
if(missedMessage5 != null && state.missedEvent5 == true){state.mmsg5 = missedMessage5}
if(missedMessage6 != null && state.missedEvent6 == true){state.mmsg6 = missedMessage6}





LOGDEBUG( "SpeakMissedNow called...")
	def prefixMsg = preMsg
	state.myMsg = prefixMsg + " ,,, " + state.mmsg1 + " ,,," + state.mmsg2 + " ,,," + state.mmsg3 + " ,,," + state.mmsg4 + " ,,," + state.mmsg5 + " ,,," + state.mmsg6
    
LOGDEBUG( "full message is: $state.myMsg")
	state.volume = volume1
    
  if (state.alreadyDone == false){  
LOGDEBUG( " Message = $state.myMsg")
	speaker.setLevel(state.volume)  
	speaker.speak(state.myMsg)
	state.alreadyDone = true
    resetMissed()
	}

  if (state.alreadyDone == true){ 
LOGDEBUG( "Already told you, so won't tell you again!")

	}
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
    	if (settings.debugMode) { log.debug("${app.label.replace(" ","_").toUpperCase()}  (Childapp Version: ${state.appversion}) - ${txt}") }
    } catch(ex) {
    	log.error("LOGDEBUG unable to output requested data!")
    }
}


// App Version   *********************************************************************************
def setAppVersion(){
    state.appversion = "1.1.0"
}