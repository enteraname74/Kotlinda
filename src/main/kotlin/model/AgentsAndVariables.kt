package com.github.enteraname74.model

/**
 *
 *
 * AGENTS
 *
 *
 */
class Capteur_H2O(vararg variable: Variable) : Agent(*variable)
class Capteur_CH4(vararg variable: Variable) : Agent(*variable)
class Capteur_CO(vararg variable: Variable) : Agent(*variable)
class Pompe(vararg variable: Variable) : Agent(*variable)
class H2O_Haut(vararg variable: Variable) : Agent(*variable)
class Commande_Pompe_Ventilateur(vararg variable: Variable) : Agent(*variable)
class Gaz_Bas(vararg variable: Variable) : Agent(*variable)
class Surveillance_Gaz_Haut(vararg variable: Variable) : Agent(*variable)
class H2O_Bas(vararg variable: Variable) : Agent(*variable)
class H2O_Diminue(vararg variable: Variable) : Agent(*variable)
class CH4_Diminue(vararg variable: Variable) : Agent(*variable)
class CO_Diminue(vararg variable: Variable) : Agent(*variable)


/**
 *
 *
 *
 * Styled methods for relaunching
 *
 *
 */

@JvmName("Capteur_H2ORec")
fun Agent.Capteur_H2O(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("Capteur_CH4Rec")
fun Agent.Capteur_CH4(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("Capteur_CORec")
fun Agent.Capteur_CO(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("PompeRec")
fun Agent.Pompe(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("H2O_HautRec")
fun Agent.H2O_Haut(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("Commande_Pompe_VentilateurRec")
fun Agent.Commande_Pompe_Ventilateur(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("Gaz_BasRec")
fun Agent.Gaz_Bas(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("Surveillance_Gaz_HautRec")
fun Agent.Surveillance_Gaz_Haut(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("H2O_BasRec")
fun Agent.H2O_Bas(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("H2O_DiminueRec")
fun Agent.H2O_Diminue(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("CH4_DiminueRec")
fun Agent.CH4_Diminue(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

@JvmName("CO_DiminueRec")
fun Agent.CO_Diminue(vararg variable: Variable) = buildNewAgentForRec(this, variable.asList())

/**
 *
 *
 * VARIABLES
 *
 *
 */

val etat = Variable("etat-pompe", "désactivée")

val activee = Variable("etat-pompe", "activée")
val desactivee = Variable("etat-pompe", "activée")

val levelCap = Variable("cap", 5)

val seuil_H2O_haut = Variable("seuil-H2O-haut", 20)

val seuil_H2O_bas = Variable("seuil-H2O-bas", 10)

val seuil_CH4 = Variable("seuil_CH4", 10)
val seuil_CO = Variable("seuil_CO", 10)


/**
 *
 *
 * GLOBAL SYSTEM VARIABLES
 *
 *
 */
var valeur_CO = 6
var valeur_CH4 = 6
var valeur_H2O = 15
