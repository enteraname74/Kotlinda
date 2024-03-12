package com.github.enteraname74

import com.github.enteraname74.model.*
import kotlinx.coroutines.*


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val ts = TupleSpace()

    val capteurH2OJob = buildCapteur_H2O(ts)
    val capteurCH4Job = buildCapteur_CH4(ts)
    val capteurCOJob = buildCapteur_CO(ts)
    val pompeJob = buildPompe(ts)
    val h2OHautJob = buildH2O_haut(ts)
    val commandePompeVentilateurJob = buildCommande_Pompe_Ventilateur(ts)
    val gazBasJob = buildGaz_bas(ts)
    val surveillanceGazHautJob = buildSurveillance_Gaz_Haut(ts)
    val h2OBasJob = buildH2O_Bas(ts)

    joinAll(
        capteurH2OJob,
        capteurCH4Job,
        capteurCOJob,
        pompeJob,
        h2OHautJob,
        commandePompeVentilateurJob,
        gazBasJob,
        surveillanceGazHautJob,
        h2OBasJob
    )
}

fun buildCapteur_H2O(ts: TupleSpace): Job {//
    return CoroutineScope(Dispatchers.IO).launch {
        Capteur_H2O() def {
            add(ts, T(s("niveau-H2O", string), v("valeur-H2O", valeur_H2O++, float)))
                .Capteur_H2O()
        }
    }
}

fun buildCapteur_CH4(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Capteur_CH4() def {
            add(ts, T(s("niveau-CH4", string), v("valeur-CH4", valeur_CH4++, float)))
                .Capteur_CH4()
        }
    }
}

fun buildCapteur_CO(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Capteur_CO() def {
            add(ts, T(s("niveau-CO", string), v("valeur-CO", valeur_CO++, float)))
                .Capteur_CO()
        }
    }
}

fun buildPompe(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Pompe(etat) def {
            println("POMPE -- État de la pompe: $state")
            b {
                pop(ts, T(s("activation-pompe", string))).Pompe(activee)
            } + b {
                pop(ts, T(s("desactivation-pompe", string))).Pompe(desactivee)
            }
        }
    }
}

fun buildH2O_haut(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        H2O_Haut(seuil_H2O_haut) def {
            read(ts, T(s("detection-H2O-haut", string)))
                .read(ts, T(s("niveau-H2O", string), r("x", float)))[v("x") >= seuil_H2O_haut] {
                println("H20 HAUT -- Niveau dépassé: ${v("x")}")
                out(ts, T(s("H2O-haut-detecte", string)))
                    .pop(ts, T(s("detection-H2O-haut", string)))
                    .H2O_Haut(seuil_H2O_haut)
            }[v("v") < seuil_H2O_haut] {
                println("H20 HAUT -- Niveau normal: ${v("x")}")
                H2O_Haut(seuil_H2O_haut)
            }
        }
    }
}

fun buildCommande_Pompe_Ventilateur(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Commande_Pompe_Ventilateur(seuil_CH4, seuil_CO) def {
            pop(ts, T(s("H2O-haut-detecte", string)))
                .read(ts, T(s("niveau-CH4", string), r("y", float)))
                .read(ts, T(s("niveau-CO", string), r("z", float)))
                .b {
                    this[(v("y") < seuil_CH4) `^` (v("z") < seuil_CO)] {
                        println("COMMANDE POMPE VENTILATEUR -- Les deux seuils de gaz sont bons")
                        out(ts, T(s("activation-pompe", string)))
                            .out(ts, T(s("detection-H2O-bas", string)))
                            .out(ts, T(s("detection-gaz-haut", string)))
                            .Commande_Pompe_Ventilateur(seuil_CH4, seuil_CO)
                    }
                } + b {
                this[(v("y") >= seuil_CH4) v (v("z") >= seuil_CO)] {
                    println("COMMANDE POMPE VENTILATEUR -- Un des deux seuils a été dépassé: ${v("y")} ${v("z")}")
                    out(ts, T(s("activation-ventilateur", string)))
                        .out(ts, T(s("detection-gaz-bas", string)))
                        .Commande_Pompe_Ventilateur(seuil_CH4, seuil_CO)
                }
            }
        }
    }
}

fun buildGaz_bas(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Gaz_Bas(seuil_CH4, seuil_CO) def {
            read(ts, T(s("detection-gaz-bas", string)))
                .read(ts, T(s("niveau-CH4", string), r("x", float)))
                .read(ts, T(s("niveau-CO", string), r("y", float)))[(v("x") < seuil_CH4) `^` (v("y") < seuil_CO)] {
                    println("GAZ BAS -- Les gazs sont bas: ${v("x")} ${v("y")}")
                    out(ts, T(s("activation-pompe", string)))
                        .out(ts, T(s("detection-H2O-bas", string)))
                        .pop(ts, T(s("detection-gaz-bas", string)))
                        .Gaz_Bas(seuil_CH4, seuil_CO)
            }[(v("x") >= seuil_CH4) v (v("y") >= seuil_CO)] {
                println("GAZ BAS -- Un des gaz a dépassé la limite: ${v("x")} ${v("y")}")
                Gaz_Bas(seuil_CH4, seuil_CO)
            }
        }
    }
}

fun buildSurveillance_Gaz_Haut(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Surveillance_Gaz_Haut(seuil_CH4, seuil_CO) def {
            read(ts, T(s("detection-gaz-haut", string)))
                .read(ts, T(r("niveau-CH4", "x", string)))
                .read(ts, T(r("niveau-CO", "y", string)))
                .b {
                    this[(v("x") >= seuil_CH4) v (v("y") >= seuil_CO)] {
                        println("SURVEILLANCE GAZ HAUT -- Un des gazs a dépassé le niveau: ${v("x")} ${v("y")}")
                        out(ts, T(s("activation-ventilateur", string)))
                            .pop(ts, T(s("detection-gaz-haut", string)))
                            .Surveillance_Gaz_Haut(seuil_CH4, seuil_CO)
                    }
                } + b {
                this[(v("x") < seuil_CH4) `^` (v("y") < seuil_CO)] {
                    println("SURVEILLANCE GAZ HAUT -- Les deux gazs sont bas: ${v("x")} ${v("y")}")
                    Surveillance_Gaz_Haut(seuil_CH4, seuil_CO)
                }
            }
        }
    }
}

fun buildH2O_Bas(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        H2O_Bas(seuil_H2O_bas) def {
            read(ts, T(s("detection-H2O-bas", string)))
                .read(ts, T(s("niveau-H2O", string), r("x", float)))
                .b {
                    this[v("x") < seuil_H2O_bas] {
                        println("H2O BAS -- Le nibeau d'eau est bas ${v("x")}")
                        out(ts, T(s("desactivation-pompe", string)))
                            .out(ts, T(s("desactivation-ventilateur", string)))
                            .pop(ts, T(s("detection-H2O-bas", string)))
                            .out(ts, T(s("detection-H2O-haut", string)))
                            .H2O_Bas(seuil_H2O_bas)
                    }
                } + b {
                this[v("x") >= seuil_H2O_bas] {
                    println("H2O BAS -- Le nibeau d'eau est normal ${v("x")}")
                    H2O_Bas(seuil_H2O_bas)
                }
            }
        }
    }
}

// TODO: Lire d'abord les valeurs avant de décrémenter

fun buildH2O_Diminue(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        H2O_Diminue() def {
            read(ts, T(s("activation-pompe", string)))
                .add(ts, T(s("niveau-H2O", string), v("valeur-H2O", valeur_H2O--, float)))
        }
    }
}

fun buildCH4_Diminue(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        CH4_Diminue() def {
            read(ts, T(s("activation-ventilateur", string)))
                .add(ts, T(s("niveau-CH4", string), v("valeur-CH4", valeur_CH4--, float)))
        }
    }
}

fun buildCO_Diminue(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        CO_Diminue() def {
            read(ts, T(s("activation-ventilateur", string)))
                .add(ts, T(s("niveau-CO", string), v("valeur-CO", valeur_CO--, float)))
        }
    }
}
