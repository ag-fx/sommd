package org.domaintbn.sommd.gui

import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.*
import tornadofx.*
import kotlin.reflect.KClass

class MainStyle2 : Stylesheet(){
    companion object {
        val CompilationLabelOK by cssclass(CustomCssStrings.compLabelOK)
        val CompilationLabelError by cssclass(CustomCssStrings.compLabelErr)
        val CompilationLabelWaiting by cssclass(CustomCssStrings.compLabelWait)


        val command by cssclass()
        val comment by cssclass()
        val branch by cssclass()
        val none by cssclass()
        val syntaxerror by cssclass()
        val variable by cssclass()


        val backGndOnText by cssclass("rtfx-background-color")


        val styledTextArea by cssclass("styled-text-area")

        val selection by cssclass("selection")



        val buttonhboxgroup by cssclass("buttonhboxgroup")

        val codearea by cssclass("code-area")

        //val selection by cssclass()

    }

    init {


        val compilationLableMixin = mixin{
            //fontWeight = FontWeight.BOLD
            textAlignment = TextAlignment.CENTER
            //fontSize = 20.px
            borderInsets += box(0.px)
            borderStyle += BorderStrokeStyle.SOLID
            borderWidth += box(4.px)
            borderRadius += box(15.px)
            backgroundRadius += box(2.px)
            //backgroundColor = multi(Color.TRANSPARENT,Color.TRANSPARENT)
            borderColor += box(Color.LIGHTGRAY)
            baseColor = Color.RED
        }


        CompilationLabelOK {
            +compilationLableMixin
            backgroundColor += Color.GREEN
            text {
                fill = Color.WHITESMOKE
            }
        }

        CompilationLabelWaiting {
            +compilationLableMixin
            backgroundColor += Color.TRANSPARENT
            //backgroundColor += Color.DARKGREY
            fill = Color.BLACK
        }

        CompilationLabelError {
            +compilationLableMixin
            backgroundColor += c("c00000")
            text {
                fill = Color.WHITESMOKE
            }
        }


        command {
            fontWeight = FontWeight.BOLD
            fill = c("0x0000f0")
        }
        comment {
            fill = Color.GREEN
        }
        branch {
            fontWeight = FontWeight.BOLD
            fontStyle = FontPosture.ITALIC
            fill = Color.DARKRED
        }

        syntaxerror {
            backgroundColor += c("c00000")
            fill = c("c00000")
            baseColor = Color.AQUA
        }

        variable {
            fill = Color.BROWN
            fontWeight = FontWeight.BOLD
        }

        none {
            //fontStyle = FontPosture.ITALIC
            //fill = Color.LIGHTGREEN
            fill = Color.BLACK
        }


        selection {
            fill = Color.WHITESMOKE

        }

        buttonhboxgroup{
            padding = box(0.px,2.px,0.px,2.px)
            //borderStyle += BorderStrokeStyle.DASHED
            //borderInsets += box(-2.px)
            //borderWidth += box(5.px)
        }

        styledTextArea {
            //backgroundColor += Color.DARKRED

            text{
                fontSmoothingType = FontSmoothingType.GRAY
            }

            selection {
                fill = Color.LIGHTGRAY
            }
        }



        codearea {

            backgroundColor += Color.WHITESMOKE
            //fontSize = 12.px
            borderStyle += BorderStrokeStyle.SOLID
        }




    }
}


class MainStyle : Stylesheet(){
    companion object {
        val CompilationLabelOK by cssclass(CustomCssStrings.compLabelOK)
        val CompilationLabelError by cssclass(CustomCssStrings.compLabelErr)
        val CompilationLabelWaiting by cssclass(CustomCssStrings.compLabelWait)

        val command by cssclass()
        val comment by cssclass()
        val branch by cssclass()
        val none by cssclass()
        val syntaxerror by cssclass()
        val variable by cssclass()

        val styledTextArea by cssclass("styled-text-area")
        val selection by cssclass("selection")

        val caret by cssclass("caret")

        val codearea by cssclass("code-area")

        val lineNumbering by cssclass("lineno")


        val buttonhboxgroup by cssclass("buttonhboxgroup")

        //val camingoCodeFont = loadFont("/CamingoCode-v1.0/CamingoCode-Regular.ttf",12)

    }

    init {

        val flOrange = c("ffe799")
        val flOrangeDarker = c("ffa456")
        val flLightGray = c("d9e1e5")
        val fldarkButtonBkg = c("474f53")
        val flWhiteish = c("e1f3f4")
        val flChannelRackGrey = c("5f686d")
        val flBrowserBrown = c("c29685")
        val flDarkGray = c("a4acb0")
        val flGreen = c("a8e44a")

        val fldarkButtonBkgDarker = c("#2d3438")


        val darkRed = c("c00000")


        val globalSettings = mixin{
            backgroundColor += fldarkButtonBkg
            fill = flWhiteish
            text{
                fill = flWhiteish
            }
        }




        button{
            +globalSettings
            //borderStyle += BorderStrokeStyle.SOLID
            backgroundColor = multi(flChannelRackGrey , fldarkButtonBkg, fldarkButtonBkgDarker)
            backgroundInsets = multi(box(4.px), box(6.px), box(8.px))
        }

        s(button, tabLabel,comboBox){
            and(hover){
                underline = true
            }
        }


//
        alert{
            +globalSettings
            headerPanel{
                +globalSettings
            }
            content{
                text{
                    textAlignment = TextAlignment.CENTER
                }
            }
            textAlignment = TextAlignment.CENTER

        }

        dialogPane{
            backgroundColor += Color.YELLOW
            headerPanel{
                +globalSettings
            }
        }

        root{
            backgroundColor += fldarkButtonBkgDarker
            //padding = box(10.px)
            fill = flWhiteish
//            selected{
//                backgroundColor += Color.YELLOW
//            }
        }

        label{
            text{
                fill = flWhiteish
            }
        }


//        s(menuBar,menu,label,tabPane,tab, scrollBar){
//            +globalSettings
//        }

        s(
            //menuBar,menu,menuItem, menuButton,
            contextMenu, menuBar, tab, tabHeaderArea, tabHeaderBackground,comboBox
        , container, comboBoxPopup, comboBoxBase,slider,
            label, dialogPane,content){
            +globalSettings

        }

        tab{
            +globalSettings

            and(selected){
                //backgroundColor += fldarkButtonBkgDarker
                text{
                    fontWeight = FontWeight.BOLD
                }
            }
           // alignment = Pos.BASELINE_CENTER
            //side = Side.RIGHT
            //alignment = Pos.CENTER
        }

        box{
            +globalSettings
        }



        tabHeaderBackground{
            borderStyle += BorderStrokeStyle.SOLID
        }

        menu{
            borderStyle += BorderStrokeStyle.DASHED

        }

        contextMenu{
            borderStyle += BorderStrokeStyle.SOLID
        }

        scrollBar{
            bar{
                backgroundColor += Color.YELLOW
                fill = Color.YELLOW
            }
            track{
                backgroundColor += flChannelRackGrey
            }
            scrollArrow{
                backgroundColor += Color.YELLOW
            }


            incrementButton{
                +globalSettings
                arrowsVisible = false
            }

            decrementButton{
                +globalSettings
                arrowsVisible = false
            }


            thumb{
                backgroundColor += flDarkGray
            }

        }


        slider{
            fill = flGreen
            track{
                backgroundColor += fldarkButtonBkg
            }
            tickLabelFill = flGreen
            tick{
                stroke = flGreen
                backgroundColor +=flBrowserBrown
                fill = flLightGray
                text{
                    stroke = flGreen
                    fill = flGreen
                }
                tickLabelFill = flGreen
            }
            backgroundColor += flDarkGray
            padding = box(10.px)

        }


        comboBox{
            listCell{
                +globalSettings
                //borderStyle += BorderStrokeStyle.SOLID
            }
            borderStyle += BorderStrokeStyle.SOLID
        }


        val compilationLabelMixin = mixin{
            //fontWeight = FontWeight.BOLD
            textAlignment = TextAlignment.CENTER
            //fontSize = 20.px
            borderInsets = multi(box(0.px),box((5).px))
            borderColor = multi(box(flChannelRackGrey),box(fldarkButtonBkg))
            borderWidth = multi(box(5.px),box(5.px))
            borderRadius = multi(box(0.px),box(10.px))

        }

        CompilationLabelOK {
            +compilationLabelMixin
            backgroundColor += flGreen
            fill = fldarkButtonBkg
        }

        CompilationLabelWaiting {
            +compilationLabelMixin
            backgroundColor += flDarkGray
            fill = fldarkButtonBkg
        }

        CompilationLabelError {
            +compilationLabelMixin
            backgroundColor += c("c00000")
            fill = flLightGray
        }

        separator{
            //fill = flBrowserBrown
            baseColor = flChannelRackGrey
        }





        command {

            fontWeight = FontWeight.BOLD
            fill = flOrange

        }
        comment {
            //fill = c("#bab960")

            fill = flDarkGray
        }
        branch {

            fontWeight = FontWeight.BOLD
            fontStyle = FontPosture.ITALIC
            fill = flOrangeDarker
        }

        variable {
            fill = flBrowserBrown
            fontWeight = FontWeight.BOLD
        }

        syntaxerror {
            backgroundColor += darkRed
            fill = darkRed

            fontWeight = FontWeight.EXTRA_BOLD
            underline = true
            baseColor = Color.AQUA

        }

        none {

            //fontStyle = FontPosture.ITALIC
            //fill = Color.LIGHTGREEN
            //fill = c("0xced4d8")
            fill = flWhiteish
        }

        codearea {

            //backgroundColor +=  c("0x5f686d")
            backgroundColor += Color.LIGHTGRAY
            //fontSize = 12.px
            borderStyle += BorderStrokeStyle.SOLID
            text {
                fill = flLightGray
            }
        }

        buttonhboxgroup{
            padding = box(0.px,5.px,0.px,5.px)
//            borderStyle += BorderStrokeStyle.SOLID
//            borderColor += box(flChannelRackGrey)
//            borderInsets += box(-2.px)
//            borderWidth += box(5.px)
        }


        styledTextArea {
            backgroundColor += fldarkButtonBkg

            selection {
                //fill = c("0000b0")
                fill = Color.DODGERBLUE
            }

            text {

                //font = Font.font("Liberation Mono")
                //fontSize = 14.px
                //camingoCodeFont?.let { fontFamily = it.family }
                fontSmoothingType = FontSmoothingType.GRAY
                backgroundColor += fldarkButtonBkg
                //fill = flLightGray

            }

            //padding = box(10.px)


            caret {
                stroke = flWhiteish
            }

            lineNumbering {
                backgroundColor += fldarkButtonBkg
                fill = flChannelRackGrey
                text {
                    fill = flDarkGray.darker()
                }
            }


        }



    }

}