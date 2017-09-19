/**
 *  Copyright 2015 SmartThings V1.2.0
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
 *  This DTH was originally developed by SmartThings for a garage door. 
 *  Then modified by Robin Winbourne for use with a dog feeder to give access to four 'states'
 *  Then modified by me (@cobra) to change the text/colours/icons to be able to use it to show who took a shared car :)
 *
 */


metadata {
	definition (name: "Custom Car Indicator - Generic", namespace: "Cobra", author: "SmartThings") {
		capability "Actuator"
		capability "Door Control"
    capability "Garage Door Control"
		capability "Refresh"
		capability "Sensor"
        
        command "driver1"
        command "driver2"
        command "on_drive"
		command "both"
       
	}

	simulator {
		
	}

	tiles {
		standardTile("toggle", "device.door", inactiveLabel: true, width: 3, height: 3) {
			state("closed", label:"Driver 1", action:"door control.open", icon:"st.People.people8", backgroundColor:"#0808F9")
			state("open", label:"Driver 2", action:"door control.close", icon:"st.People.people2", backgroundColor:"#F908F1")
			state("opening", label:"On Drive", icon:"st.Transportation.transportation12", backgroundColor:"#29BA29")
			state("closing", label:"Both", icon:"st.Transportation.transportation8", backgroundColor:"#FEC003")
			
		}
		standardTile("open", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:"On Drive", action:"door control.open", icon:"st.Transportation.transportation12"
		}
		
	
        standardTile("driver2", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Driver 2", action:"woman", icon:"st.People.people2"
		}
        standardTile("driver1", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Driver 1", action:"man", icon:"st.People.people8"
		}
	standardTile("close", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Both", action:"door control.close", icon:"st.Transportation.transportation8"
		}


		main "toggle"
		details(["toggle", "open", "close", "driver1", "driver2"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

def open() {
	sendEvent(name: "door", value: "opening")
}

def close() {
    sendEvent(name: "door", value: "closing")
}

def driver1() {
    sendEvent(name: "door", value: "closed")
}

def driver2() {
    sendEvent(name: "door", value: "open")
}

def on_drive() {
    sendEvent(name: "door", value: "opening")
}

def both() {
    sendEvent(name: "door", value: "closing")
}