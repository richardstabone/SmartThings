/**
 *  test debug
 *
 *  Copyright 2017 Andrew Parker
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
    name: "A1 test debug",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "test debug switch",
  	category: "Fun & Social",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
    )


preferences {

 page(name: "mainPage", 
     title: "$app.label",
     install: false,
     uninstall: true)  {
      
     
     // next pages here (listed on 1st page)
     section(){ 
     href "namingPage", title:"Name app, configure modes and enable/disable logging", description:"Tap to configure"
    }
     section(){  
     href "inputPage", title:"Configure Inputs", description:"Tap to configure"
    }
    
    }
    
page(name: "inputPage", 
     title: "Select inputs",
     install: false, 
     uninstall: true) {
 
 
 }

page(name: "namingPage", 
     title: "Name app, configure modes and enable/disable logging",
     install: true, 
     uninstall: true) {
          
    
		section(){           
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)", required: false
            input "debugmode", "bool", title: "Enable logging", required: true, defaultValue: false
        	   }
               
		}
}	

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
log.debug "go"
setAppVersion()
LOGINFO("Initialised with settings: ${settings}")
    
    
    
    
}



// LOGGING
def LOGDEBUG(txt){
    try {
    	if (settings.debugmode) { log.debug("${app.label.replace(" ","_").toUpperCase()}  (Version: ${state.appversion}) - ${txt}") }
    } catch(ex) {
    	log.error("LOGDEBUG: unable to output requested data!")
    }
}
def LOGINFO(txt){
    try {
    if (settings.debugmode) { log.info("${app.label.replace(" ","_").toUpperCase()}  (Version: ${state.appversion}) - ${txt}") }
        } catch(ex) {
    	log.error("LOGINFO: unable to output requested data!")
    }
}
def LOGERROR(txt){
    try {
   log.error("${app.label.replace(" ","_").toUpperCase()}  (Version: ${state.appversion}) - ERROR: ${txt}")
    } catch(ex) {
    	log.error("LOGERROR: unable to output requested data!")
    }
}


// APP VERSION
def setAppVersion(){
	
    state.appversion = "1.0.0"
}