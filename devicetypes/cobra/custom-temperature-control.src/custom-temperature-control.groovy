/**
 *  ****************  Custom Temperature Control  ****************
 *
 *	
 *  Design Usage:
 *  This was designed to be used with 'Temperature Coltrolled Switch' smartapp
 *  Creating a virtual device with this DTH enables the app to set the required temperature 'on the fly'
 *
 *  Copyright 2017 Andrew Parker
 *  
 *  This DTH is free!
 *  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://www.paypal.me/smartcobra
 *  
 *
 *  I'm very happy for you to use this DTH without a donation, but if you find it useful then it would be nice to get a 'shout out' on the forum! -  @Cobra
 *  Have an idea to make this DTH better?  - Please let me know :)
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
 *  Created: 27/01/2018
 *  Last Update: 31/01/2018
 *
 *  Changes:
 *
 * 
 *
 *
 *  V1.1.0 - Added correct variable background colours & comments
 *  V1.0.0 - POC
 *
 */


metadata {
	
	definition (name: "Custom Temperature Control", namespace: "Cobra", author: "AJ Parker") {
		capability "Temperature Measurement"
		capability "Switch Level"
		capability "Sensor"

		command "up"
		command "down"
        command "setTemperature", ["number"]
	}


/** 
* This DTH is, by default, setup for Centigrade.
* To change for fahrenheit just change the C to an F where marked
* The default starting point can also be changed.
*
*/

	tiles {
		multiAttributeTile(name:"temperature", type: "generic", width: 4, height: 4){
			tileAttribute ("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("temperature", label:'${currentValue}°', unit:"C", // change 'unit' setting to F for fahrenheit   action:"wait", 
                backgroundColors:[
                    // Celsius Color Range
                [value:  0, color: "#153591"],
                [value:  7, color: "#1E9CBB"],
                [value: 15, color: "#90D2A7"],
                [value: 23, color: "#44B621"],
                [value: 29, color: "#F1D801"],
                [value: 33, color: "#D04E00"],
                [value: 36, color: "#BC2323"],
                // Fahrenheit Color Range
                [value: 40, color: "#153591"],
                [value: 44, color: "#1E9CBB"],
                [value: 59, color: "#90D2A7"],
                [value: 74, color: "#44B621"],
                [value: 84, color: "#F1D801"],
                [value: 92, color: "#D04E00"],
                [value: 96, color: "#BC2323"]
            ]
                
            )
			}
		}
        
        
		standardTile("up", "device.temperature", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'turn up', action:"up"
		}        
		standardTile("down", "device.temperature", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'turn down', action:"down"
		}
        main "temperature"
		details("temperature","up","down")
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	def pair = description.split(":")
	createEvent(name: pair[0].trim(), value: pair[1].trim(), unit:"C") // change 'unit' setting to F for fahrenheit
}

def setLevel(value) {
	sendEvent(name:"temperature", value: value)
}

def up() {
	def ts = device.currentState("temperature")
	def value = ts ? ts.integerValue + 1 : 15  // 15 = default starting point when 1st installed. Can be changed.
	sendEvent(name:"temperature", value: value)
}

def down() {
	def ts = device.currentState("temperature")
	def value = ts ? ts.integerValue - 1 : 15  // 15 = default starting point when 1st installed. Can be changed.
	sendEvent(name:"temperature", value: value)
}

def setTemperature(value) {
	sendEvent(name:"temperature", value: value)
}