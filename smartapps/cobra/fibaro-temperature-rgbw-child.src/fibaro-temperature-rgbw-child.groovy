/**
 *  ****************   Fibaro Temperature RGBW  ****************
 *
 *  Design Usage:
 *  This is the 'Child' app for temperature controlled fibaro..
 *
 *
 *  Copyright 2018 Andrew Parker.
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
 *  Last Update: 28/02/2018
 *
 *  Changes:
 *
 * 
 *  V1.0.0 - POC
 *
 */
 
 
 
 
 
definition(
    name: "Fibaro_Temperature_RGBW_Child",
    namespace: "Cobra",
    author: "Andrew Parker",
    description: "Child - sends temperature setting to parent",
    category: "",
    
    parent: "Cobra:Fibaro Temperature Colour Changer",
    
    iconUrl: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    iconX2Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png",
    iconX3Url: "https://raw.githubusercontent.com/cobravmax/SmartThings/master/icons/cobra3.png")

preferences {

	
    
    section() {
		input "temperatureSensor1", "capability.temperatureMeasurement" , title: "Select Temperature Sensor", required: true
        input "temperature1", "number", title: "Temperature to trigger this colour", required: true
	} 
    
          
      section("Custom colour (For this temperature)"){         
		input "redDim2", "number", title: "How much RED? (0-100)", defaultValue: "0", multiple: false, required: false
        input "greenDim2", "number", title: "How much GREEN? (0-100)", defaultValue: "0", multiple: false, required: false
        input "blueDim2", "number", title: "How much BLUE? (0-100)", defaultValue: "0", multiple: false, required: false
        input "whiteDim2", "number", title: "How much White? (0-100)", defaultValue: "0", multiple: false, required: false
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
	log.debug "Initialised with settings: ${settings}"
    
    subscribe(temperatureSensor1, "temperature", temperatureHandler)
    
    
}


def temperatureHandler(evt) {
	def newTemp1 = evt.doubleValue 
    state.newTemp = newTemp1 as int
   log.info " Temperature is $state.newTemp degrees"
    def reqTemp = temperature1
    
    if(state.newTemp == reqTemp){
    log.info "Sending temp and colour to Parent "
	parent.setTempColour(reqTemp, redDim2, blueDim2, greenDim2, whiteDim2)
	}
    
}