package org.domaintbn.sommd.core.parsing

class ParserException(val errorSpot: SubString,
                      val errorMessage: ErrorMessage,
                      val errorSource : IParser
) : Exception() {

}
