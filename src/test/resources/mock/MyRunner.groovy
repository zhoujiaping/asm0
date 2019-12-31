package mock

import mock.Person

class MyRunner{
	def run(){
		def res = new Person().hello("test")
		def filename = Person.getResource("Person.groovy").file
		//new File(filename).setLastModified(System.currentTimeMillis())
		println new File(filename).lastModified()
		println "$res 4"
	}
}