package org.domaintbn.sommd.core.parsing.parserimplementations

import org.domaintbn.sommd.core.parsing.IParser
import org.domaintbn.sommd.core.parsing.SubString

class BracketParserException(val problemToken: SubString, val source: IParser) : Throwable() {

}
