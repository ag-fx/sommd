package org.domaintbn.sommd.core.parsing

import org.domaintbn.sommd.core.musical.MusicTime
import org.domaintbn.sommd.core.musical.Playhead
import org.domaintbn.sommd.core.musical.TimelineNote

/**
 * Alternative name: PlayheadCoordinator
 * Can handle sequences of commands and delegate those to appropriate Playhead(s)
 * to generate a timeline of notes
 */
class MasterPlayhead() {

    private class BranchHandler() {
        private var phStartState = mutableListOf<Playhead>()
        private var phEndState = mutableListOf<Playhead>()

        private var phIsReset = mutableListOf<Boolean>()





        fun startBranch(playhead: Playhead): Playhead {
            this.phIsReset.add(false)
            this.phStartState.add(playhead.getCopy())
            return playhead
        }

        fun resetBranch(playhead: Playhead): Playhead {
            if (!phIsReset.last()) {
                phEndState.add(playhead.getCopy())
                phIsReset[phIsReset.size-1] = true
            }
            return phStartState.last().getCopy()
        }

        fun endBranch(playhead: Playhead): Playhead {

            if (phIsReset.last()) {
                val out: Playhead = phEndState.last()
                this.phEndState.removeAt(phEndState.size - 1)
                this.phStartState.removeAt(phStartState.size - 1)
                phIsReset.removeAt(phIsReset.size-1)
                return out


            } else {
                this.phStartState.removeAt(phStartState.size - 1)
                phIsReset.removeAt(phIsReset.size-1)
                return playhead.getCopy()
            }

        }

        fun isInsideBranch(): Boolean {
            return phStartState.isNotEmpty()
        }
    }

    private val branchHandler = BranchHandler()

    private fun processLocal(commseq: CommandSeq): List<TimelineNote> {
        val output = mutableListOf<TimelineNote>()
        var currPlayH = Playhead()
        for (comm in commseq) {
            when (comm.commandType) {
                CommandType.REGULAR -> {
                    val x = comm.applyOn(currPlayH)
                    output.addAll(x)
                }

                CommandType.BRANCH_START -> {
                    currPlayH = this.branchHandler.startBranch(currPlayH)
                }


                CommandType.BRANCH_RESET -> {
                    currPlayH = this.branchHandler.resetBranch(currPlayH)
                }
                CommandType.BRANCH_END -> {
                    currPlayH = this.branchHandler.endBranch(currPlayH)
                }
            }

        }

        return output.toList()
    }


    fun process(commseq: CommandSeq): List<TimelineNote> {

        return processLocal(commseq)


    }

    class MoreInfo(val x : CommandType, val y : TimelineNote?, val mt : MusicTime){
        fun printMe(): String {
            val noteTxt = y?.printMe() ?: ""
            return "$mt , ${x.name} $noteTxt"
        }
    }
    fun processDetailed(commseq: CommandSeq) : List<MoreInfo>{
        val output = mutableListOf<MoreInfo>()
        var currPlayH = Playhead()
        for (comm in commseq) {
            when (comm.commandType) {
                CommandType.REGULAR -> {
                    val x = comm.applyOn(currPlayH)
                    if(x.isNotEmpty()) {
                        output.addAll(x.map { MoreInfo(CommandType.REGULAR, it, currPlayH.timePos.getCopy()) })
                    }else{
                        output.add(MoreInfo(CommandType.REGULAR,null,currPlayH.timePos.getCopy()))
                    }
                }

                CommandType.BRANCH_START -> {
                    currPlayH = this.branchHandler.startBranch(currPlayH)
                    output.add(MoreInfo(comm.commandType,null,currPlayH.timePos.getCopy()))
                }


                CommandType.BRANCH_RESET -> {
                    currPlayH = this.branchHandler.resetBranch(currPlayH)
                    output.add(MoreInfo(comm.commandType,null,currPlayH.timePos.getCopy()))
                }
                CommandType.BRANCH_END -> {
                    currPlayH = this.branchHandler.endBranch(currPlayH)
                    output.add(MoreInfo(comm.commandType,null,currPlayH.timePos.getCopy()))
                }
            }

        }

        return output.toList()
    }





}