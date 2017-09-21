/**
 *  ****************  Message_Central_Child  ****************
 *
 *  Design Usage:
 *  This is the 'Child' app for message automation
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
 *  Last Update: 08/09/2017
 *
 *  Changes:
 *
 *  V1.2.2 - New Icons
 *  V1.2.1 - Debug - Time did not have day restriction
 *  V1.2.0 - Added switchable logging
 *	V1.1.0 - Added delay between messages
 *  V1.0.2 - Debug
 *  V1.0.1 - Header & Debug
 *  V1.0.0 - POC
 *
 */

definition(
    name: "Message_Central_Child",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Child App for Message Automation",
    category: "My Apps",

   
    
    parent: "Cobra:Message Central",
    iconUrl: "http://54.246.165.27/img/icons/cobra3.png",
    iconX2Url: "http://54.246.165.27/img/icons/cobra3.png",
    iconX3Url: "http://54.246.165.27/img/icons/cobra3.png")

preferences {
    page name: "mainPage", title: "", install: false, uninstall: true, nextPage: "namePage"
    page name: "namePage", title: "", install: true, uninstall: true
}

def installed() {
    initialize()
}

def updated() {
    unschedule()
    initialize()
}

def initialize() {
	  log.info "Initialised with settings: ${settings}"
      setAppVersion()
      logCheck()
      state.appgo = true
      state.timer1 = true
      state.timer2 = true
     
// Subscriptions    

subscribe(enableSwitch, "switch", switchEnable)

if(trigger == 'Time'){
   LOGDEBUG("Trigger is $trigger")
   schedule(runTime,timeTalkNow)
    }
else if(trigger == 'Switch'){
     LOGDEBUG( "Trigger is $trigger")
subscribe(switch1, "switch", switchTalkNow)
    }
else if(trigger == 'Water'){
    LOGDEBUG( "trigger is $trigger")
subscribe(water1, "water.wet", waterTalkNow) 
subscribe(water1, "water.dry", waterTalkNow) 
	}
else if(trigger == 'Contact'){
    LOGDEBUG( "trigger is $trigger")
subscribe(contactSensor, "contact", contactTalkNow) 
	}
else if(trigger == 'Presence'){
    LOGDEBUG("trigger is $trigger")
subscribe(presenceSensor1, "presence", presenceTalkNow) 
     
	}
    
}


// main page *************************************************************************
def mainPage() {
    dynamicPage(name: "mainPage") {
      section() {
   
        paragraph image: "http://54.246.165.27/img/icons/cobra3.png",
                         "Child Version: $state.appversion - Brought to you by Cobra"
    }
        section {
        paragraph image: "http://54.246.165.27/img/icons/voice.png",
                  title: "Message Control Child",
                  required: false,
                  "This child app allows you use different triggers to create different messages"
                  
     
        	speakerInputs()
            triggerInput()
            actionInputs()
        }
         }
}


def namePage() {
       dynamicPage(name: "namePage") {
       
            section("Automation name") {
                label title: "Enter a name for this message automation", required: false
            }
             section("Modes") {
           		mode title: "Set for specific mode(s)", required: false
            }
             section("Logging") {
            input "debugMode", "bool", title: "Enable logging", required: true, defaultValue: false
  	        }
      }  
    }



// defaults
def speakerInputs(){	
	input "enableSwitch", "capability.switch", title: "Select switch Enable/Disable this message", required: false, multiple: false 
	
	input "speaker", "capability.musicPlayer", title: "Choose speaker(s)", required: true, multiple: true
	input "volume", "number", title: "Speaker volume", description: "0-100%", defaultValue: "100",  required: true
	


}




// inputs
def triggerInput() {
   input "trigger", "enum", title: "How to trigger message?",required: true, submitOnChange: true, options: ["Time", "Switch", "Presence", "Water", "Contact",]
  
}


def actionInputs() {
    if (trigger) {
    state.selection = trigger
    
    
if(state.selection == 'Switch'){
    input "switch1", "capability.switch", title: "Select switch to trigger message", required: false, multiple: false 
	input "message1", "text", title: "Message to play when switched on",  required: false
	input "message2", "text", title: "Message to play when switched off",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", defaultValue: '0', description: "Seconds", required: true
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
    input "fromTime", "time", title: "Allow messages from", required: true
    input "toTime", "time", title: "Allow messages until", required: true
    input "days", "enum", title: "Select Days of the Week", required: true, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
}    
 

else if(state.selection == 'Water'){
	input "water1", "capability.waterSensor", title: "Select water sensor to trigger message", required: false, multiple: false 
	input "message1", "text", title: "Message to play when WET",  required: false
	input "message2", "text", title: "Message to play when DRY",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", description: "Seconds", required: true
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	input "fromTime", "time", title: "Allow messages from", required: true
    input "toTime", "time", title: "Allow messages until", required: true
    input "days", "enum", title: "Select Days of the Week", required: true, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
}   

else if(state.selection == 'Presence'){
	input "presenceSensor1", "capability.presenceSensor", title: "Select presence sensor to trigger message", required: false, multiple: false 
	input "message1", "text", title: "Message to play when sensor arrives",  required: false
	input "message2", "text", title: "Message to play when sensor leaves",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", defaultValue: "0", description: "Seconds", required: true
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	input "fromTime", "time", title: "Allow messages from", required: true
    input "toTime", "time", title: "Allow messages until", required: true
    input "days", "enum", title: "Select Days of the Week", required: true, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
}   
else if(state.selection == 'Contact'){
	input "contactSensor", "capability.contactSensor", title: "Select contact sensor to trigger message", required: false, multiple: false 
	input "message1", "text", title: "Message to play when sensor opens",  required: false
	input "message2", "text", title: "Message to play when sensor closes",  required: false
    input "triggerDelay", "number", title: "Delay after trigger before speaking (Enter 0 for no delay)", description: "Seconds", required: true
    input "msgDelay", "number", title: "Delay between messages (Enter 0 for no delay)", defaultValue: '0', description: "Minutes", required: true
	input "fromTime", "time", title: "Allow messages from", required: true
    input "toTime", "time", title: "Allow messages until", required: true
    input "days", "enum", title: "Select Days of the Week", required: true, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
}   
else if(state.selection == 'Time'){
	input (name: "runTime", title: "Time to run", type: "time",  required: true) 
	input "messageTime", "text", title: "Message to play",  required: true
    input "days", "enum", title: "Select Days of the Week", required: true, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday", "Saturday": "Saturday", "Sunday": "Sunday"]
	
}   





}
}





// Handlers

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



// Enable Switch

def switchEnable(evt){
state.sEnable = evt.value
LOGDEBUG("$enableSwitch = $state.sEnable")
if(state.sEnable == 'on'){
state.appgo = true
}
else if(state.sEnable == 'off'){
state.appgo = false
}
}


// Time
def timeTalkNow(evt){
checkDay()
if(state.appgo == true && state.dayCheck == true){
LOGDEBUG("Time trigger -  Activating now! ")
def msg = messageTime
speaker.setLevel(volume)
LOGDEBUG( "Speaker(s) in use: $speaker set at: $volume%"  )
speaker.speak(msg)
}
else if(state.appgo == false){
LOGDEBUG( "$enableSwitch is off so cannot continue")
}
else if(state.dayCheck == false){
LOGDEBUG( "Cannot continue - Daycheck failed")
}


}



// Switch
def switchTalkNow(evt){
state.talkswitch = evt.value
state.msg1 = message1
state.msg2 = message2

if(state.talkswitch == 'on'){
if(state.msg1 != null){
state.msgNow = 'oneNow'
    }
}
else if (state.talkswitch == 'off'){
if(state.msg2 != null){
state.msgNow = 'twoNow'
	}
}
LOGDEBUG( "$switch1 is $state.talkswitch")
def mydelay = triggerDelay
speaker.setLevel(volume)
LOGDEBUG("Speaker(s) in use: $speaker set at: $volume% - waiting $mydelay seconds before continuing..."  )
runIn(mydelay, talkSwitch)

}


// Contact
def contactTalkNow(evt){
state.talkcontact = evt.value
state.msg1 = message1
state.msg2 = message2

if(state.talkcontact == 'open'){
if(state.msg1 != null){
state.msgNow = 'oneNow'
}
}
else if (state.talkcontact == 'closed'){
if(state.msg2 != null){
state.msgNow = 'twoNow'
}
}
LOGDEBUG("$contactSensor is $state.talkcontact")
def mydelay = triggerDelay
speaker.setLevel(volume)
LOGDEBUG( "Speaker(s) in use: $speaker set at: $volume% - waiting $mydelay seconds before continuing..."  )
runIn(mydelay, talkSwitch)

}
// Water
def waterTalkNow(evt){
state.talkwater = evt.value
state.msg1 = message1
state.msg2 = message2

if(state.talkwater == 'wet'){
if(state.msg1 != null){
state.msgNow = 'oneNow'
	}
}
else if (state.talkwater == 'dry'){
if(state.msg2 != null){
state.msgNow = 'twoNow'
	}
}
LOGDEBUG( "$water1 is $state.talkwater")
def mydelay = triggerDelay
speaker.setLevel(volume)
LOGDEBUG( "Speaker(s) in use: $speaker set at: $volume% - waiting $mydelay seconds before continuing..."  )
runIn(mydelay, talkSwitch)

}

// Presence
def presenceTalkNow(evt){
state.talkpresence = evt.value
state.msg1 = message1
state.msg2 = message2


if(state.talkpresence == 'present'){
if(state.msg1 != null){
state.msgNow = 'oneNow'
	}
}
else if (state.talkpresence == 'not present'){
if(state.msg2 != null){
state.msgNow = 'twoNow'
	}
}
LOGDEBUG( "$presenceSensor1 is $state.talkpresence")
def mydelay = triggerDelay
speaker.setLevel(volume)
LOGDEBUG("Speaker(s) in use: $speaker set at: $volume% - waiting $mydelay seconds before continuing..."  )
runIn(mydelay, talkSwitch)

}



// Talk now....

def talkSwitch(){
checkTime()
checkDay()
LOGDEBUG("state.appgo = $state.appgo - state.timeOK = $state.timeOK - state.dayCheck = $state.dayCheck - state.timer1 = $state.timer1 - state.timer2 = $state.timer2")
if(state.appgo == true && state.timeOK == true && state.dayCheck == true){

LOGDEBUG( " Continue... Check delay...")

if(state.msgNow == 'oneNow' && state.timer1 == true){
LOGDEBUG("All OK! - Playing message 1: '$state.msg1'")
speaker.speak(state.msg1)
startTimer1()
}
else if(state.msgNow == 'twoNow'  && state.msg2 != null && state.timer2 == true){
LOGDEBUG( "All OK! - Playing message 2: '$state.msg2'")
speaker.speak(state.msg2)
startTimer2()
}
else if(state.appgo == false){
LOGDEBUG("$enableSwitch is off so cannot continue")
}

else if(state.timeOK == false){
LOGDEBUG("Not enabled for this time so cannot continue")
}

else if(state.msgNow == 'oneNow' && state.msg1 == null){
LOGDEBUG( "Message 1 is empty so nothing to say")
}
else if(state.msgNow == 'twoNow' && state.msg2 == null){
LOGDEBUG( "Message 2 is empty so nothing to say")
}
}



}


// Check time allowed to run...

def checkTime(){

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

def checkDay(){

 def df = new java.text.SimpleDateFormat("EEEE")
    
    df.setTimeZone(location.timeZone)
    def day = df.format(new Date())
    def dayCheck1 = days.contains(day)
    if (dayCheck1) {

  state.dayCheck = true
LOGDEBUG( " Day ok so can continue...")
 }       
 else {
LOGDEBUG( " Not today!")
 state.dayCheck = false
 }
 }
 
 // Delay between messages...

def startTimer1(){
state.timer1 = false
state.timeDelay = 60 * msgDelay
LOGDEBUG("Waiting for $state.timeDelay seconds before resetting timer1 to allow further messages")
runIn(state.timeDelay, resetTimer1)
}

def startTimer2(){
state.timer2 = false
state.timeDelay = 60 * msgDelay
LOGDEBUG( "Waiting for $state.timeDelay seconds before resetting timer2 to allow further messages")
runIn(state.timeDelay, resetTimer2)
}

def resetTimer1() {
state.timer1 = true
LOGDEBUG( "Timer 1 reset - Messages allowed")
}
def resetTimer2() {
state.timer2 = true
LOGDEBUG("Timer 2 reset - Messages allowed")
}


// App Version   *********************************************************************************
def setAppVersion(){
    state.appversion = "1.2.2"
}
