package org.domaintbn.sommd.core.parsing

class RecursiveList<T>(){

    constructor(value : String) : this(){
        makeSingleton(value)
    }

    private val contentLocal = mutableListOf<RecursiveList<T>>()
    val content : List<RecursiveList<T>> get() = contentLocal.toList()

    private var valueLocal : String? = null
    val value : String get(){
        if(isSingleton){
            return valueLocal!!
        }else{
            error("Only allowed to get value from a singleton")
        }
    }

    fun append(element : RecursiveList<T>){
        if(!isSingleton){
            contentLocal.add(element)
        }else{
            error("Append after making singleton is not allowed")
        }
    }

    fun makeSingleton(value : String){
        if(!isSingleton) {
            this.valueLocal = value
        }else{
            error("Can only mark as singleton once")
        }
    }

    val isSingleton : Boolean get() = this.valueLocal!=null && contentLocal.isEmpty()
    var repeatCnt: Int = 1

    val isEmpty : Boolean get(){
        return this.contentLocal.isEmpty()
    }

}
