/**
 *  
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


	tiles {
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'${currentValue}', unit:"C",
				backgroundColors:[
					[value: 11, color: "#153591"],
					[value: 15, color: "#1e9cbb"],
					[value: 20, color: "#33D926"],
					[value: 25, color: "#44b621"],
					[value: 30, color: "#f1d801"],
					[value: 35, color: "#d04e00"],
					[value: 40, color: "#bc2323"]
				]
			)
		}
		standardTile("up", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'up', action:"up"
		}        
		standardTile("down", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'down', action:"down"
		}
        main "temperature"
		details("temperature","up","down")
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	def pair = description.split(":")
	createEvent(name: pair[0].trim(), value: pair[1].trim(), unit:"C")
}

def setLevel(value) {
	sendEvent(name:"temperature", value: value)
}

def up() {
	def ts = device.currentState("temperature")
	def value = ts ? ts.integerValue + 1 : 15 
	sendEvent(name:"temperature", value: value)
}

def down() {
	def ts = device.currentState("temperature")
	def value = ts ? ts.integerValue - 1 : 15 
	sendEvent(name:"temperature", value: value)
}

def setTemperature(value) {
	sendEvent(name:"temperature", value: value)
}