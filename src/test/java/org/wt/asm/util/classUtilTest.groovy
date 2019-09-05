package org.wt.asm.util

import org.objectweb.asm.Type

def showCns(){
	def pts = [boolean,byte,short,char,int,long,float,double]
	def cs = [long,int[],long[][],String,String[],String[][]]
	cs.forEach(c->{
		println "${c} => "+c
		println "${c}.cn => "+c.name
		println "${c}.tcn => "+Type.getType(c).className
		println "${c}.ctn => "+c.typeName
		try{
			println "${c}.icn => "+Type.getType(c).internalName
		}catch(e){
			println "${c}.icn => undefined"
		}

		println "${c}.desc => "+Type.getType(c).descriptor
		println "${c}.size => "+Type.getType(c).size
		println "="*20
	})
}
showCns()