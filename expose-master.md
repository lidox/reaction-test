# Exposé Masterarbeit - Artur Schäfer
Version 1.0

## Problemstellung
An der Universitätsklinik Düsseldorf werden sogenannte „Wachoperationen“ durchgeführt, um beispielsweise einen Gehirntumor zu entfernen. Die Patienten (i.d.R. Krebspatienten) werden im Wachzustand operiert. Während der Gehirnoperation steht der Neurochirurg hinter dem Patienten. Er kann mit dem Patienten kommunizieren, jedoch kann er diesen nicht von vorne sehen. Folglich können technische Hilfsmittel, die den Zustand des Patienten verfolgen, entscheidend helfen. 
Durch den Einsatz geeigneter Apps können Hirntumor-Patienten auf neurokognitiver Ebene verfolgt / „getrackt“ werden, um praxisrelevante Erkenntnisse zu erlangen.

## Forschungsstand
Derzeit ist keine Forschung bekannt, die mittels App-basierte Reaktionstests einen Mehrwert in der Neuroonkologie hervorbringen. Allgemeine Reaktionstest hingegen, werden in vielen Bereichen der Wissenschaft eingesetzt und statistisch ausgewertet.

In der Projektarbeit wurde unteranderem ein Prototyp der Reaktions-App entwickelt, welches während der Masterarbeit ausgebaut werden kann.

Im Anschluss zu der Masterarbeit ist das Universitätsklinikum Düsseldorf an einer Zusammenarbeit mit anschließender Veröffentlichung eines Papers interessiert. 


## Wissenslücke/ Erkenntnisinteresse
Die Ärzte in der Neuroonkologie haben ein Interesse daran technisch unterstützt zu werden, damit sie sich auf das Wesentliche ihrer Arbeit konzentrieren können. Beispielsweise könnte eine signifikante Veränderung des Gemütszustandes des Patienten während einer Operation, oder der Zeitpunkt des optimalen Wachheitsgrades des Patienten vor der Operation, ermittelt werden. Die neugewonnen Informationen können den Ärzten helfen.

## Fragestellung 
Folgende Fragestellungen kommen in Frage:

1. Sind mobile Apps unter Berücksichtigung der Umstände in der Klinik sinnvoll? (Unterschiedliche Medikamentenzufuhr, Altersstufen, Kultur, technische Affinität, psychische Belastung etc.)

2. Wie können während einer Wachoperation signifikante Änderungen des Gemütszustandes eines Patienten ermittelt werden und den Ärzten mitgeteilt werden?

3. Wie kann der Wachheitsgrad eines Patienten mittels einer mobilen Applikation ermittelt werden?


## Ziel/Hypothese
Ziel der Masterarbeit ist zu zeigen, dass der Einsatz der entwickelten App einen "Mehrwert" für die Klinik darstellt. Die App sammelt Patientendaten vor, während und nach einer Operation. Außerdem können Gemütszustandsveränderungen eines Patienten erkannt werden. Die neugewonnen Informationen können den Ärzten bei vielen Entscheidungen helfen. 

## Methode
Damit Signifikanzen erkannt sowie Daten gesammelt werden können, muss die App zuverlässig und fehlerfrei sein. Zusätzlich muss sie Benutzerfreundlichkeit berücksichtigen.

Folglich müssen automatisierte Tests entwickelt werden.

Außerdem ist die ständige Kommunikation zwischen Patienten, Doktoren und dem Entwickler wichtig, damit die Applikation erweitert werden kann. Eventuell kann eine Befragung der Patienten / Doktoren durchgeführt werden, um die Zufriedenheit zu ermitteln.

Des Weiteren müssen geeignete statistische Verfahren ermittelt und eingesetzt werden. Beispielsweise sollte ein statistisches Model für Reaktionstestdaten ermittelt werden.

## Vorläufige Gliederung
### 1. Einleitung
### 1.1 Motivation
### 1.2 Kontext
### 1.3 Ziel
### 2. Stand der Praxis
### 3. Ermittlung von Informationen
### 3.1 Ermittlung des Wachheitsgrades
### 3.2 Ermittlung von Signifikanten Änderungen in der OP
### 4. Evaluation 
- Hat die App ein Nutzen / ist sie aus klinischer Sicht relevant
- Sind Reaktionstests intraoperativ sinnvoll? Der Patient hat kurz vorher eine Vollnarkose erhalten und befindet sich in Schräglage während der OP.
- Erfüllt die mobile Applikation ihren Zweck trotz der Umstände in der Klinik?
- Reicht es ausschließlich das frontale Gehirnareal zu testen? 

Folgende Areale kommen in Frage und könnten getestet werden:
- Temporal
- Occipital
- Parietal
- Central
- Cerebellar
- Hippocampal
- Language

### 5. Fazit

### 6. Ausblick
- Durch Sammlung der Daten können später Analysen durchgeführt werden


## Zeitplan
Anmeldung am 01.04.2017

Späteste Abgabe am 01.10.2017

## Mögliche Erweiterungen der APP
Im Rahmen der Projektarbeit wurde ein Prototyp entwickelt. Dieser kann folgendermaßen ausgebaut werden:
* Zusätzliche „Games“ für das frontale Hirnareal:
    * [Go-No-Go Game](http://cognitivefun.net/test/17)
    * [True Color](https://www.youtube.com/watch?v=ZOSyBsx1l6s)
    * [Low Pop](http://www.ilovefreesoftware.com/08/iphone/iphone-brain-training-game-improve-brain-skills.html)
* Tests: Unit, Monkey, UI
* UI für unterschiedliche Displaygrößen anpassen
* Refactoring / Optimierungen 
	* z.B. Recyclerview statt Listview
* Anbindung an eine zentrale Datenbank, da mehrere Tablets eingesetzt werden
	* Visualisierung aller Daten 
