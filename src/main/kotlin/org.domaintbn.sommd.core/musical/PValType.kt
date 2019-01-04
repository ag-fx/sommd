package org.domaintbn.sommd.core.musical

enum class PValType(val validate: (x: Any) -> Boolean) {


    INT({ (it is Int) }),

    PERCENTUNI({ it is Double && it in 0.0..1.0 }),

    PERCENTBI(({ it is Double && it in -1.0..1.0 })),

    BOOL({ it is Boolean }),

    SCALE({ it is Scale })
//    {
//        override fun validate2(s : String): Boolean {
//            return true
//        }
//    };

    //abstract fun validate2(s : String) : Boolean

}