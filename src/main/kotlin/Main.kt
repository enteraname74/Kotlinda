package com.github.enteraname74

import com.github.enteraname74.model.*
import kotlinx.coroutines.*


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val ts = TupleSpace()

    Agent(levelCap, etat) def {
        read(ts, T(r("level", "x", integer)))[
            v("x") > levelCap
        ] {
            println("X is superior to 5, x = ${v("x")}")
        }[v("x") < levelCap] {
            println("X is less than 5, x = ${v("x")}")
        }
            .out(ts, T(v("other", "Hello", string)))
            .read(ts, T(s("other", string)))
            .pop(ts, T(s("level", integer)))
            .read(ts, T(r("level", "x", integer)))
    }

//    Agent(levelCap) def {
//        j {
//            read(ts, T(r("level", "x", integer))).Agent(levelCap)
//        } + j{
//            read(ts, T(r("amogus", "z", integer))).Agent(levelCap)
//        }
//    }

//    Agent(levelCap) def {
//        simplePlus(
//            b1 = {
//                println("B1")
//                read(ts, T(r("level", "x", integer)))
//            },
//            b2 = {
//                println("B2")
//                read(ts, T(r("amogus", "x", integer)))
//            }
//        ).Agent(levelCap)
//    }

    Agent(levelCap) def {
        b {
            println("B1")
            read(ts, T(r("amogus", "x", integer)))
        } + b {
            println("B2")
            read(ts, T(r("level", "x", integer)))
        }
        Agent(levelCap)
    }

//    Agent(levelCap) def {
//         {
//            println("B1")
//            read(ts, T(r("amogus", "x", integer)))
//        } + runBlocking {
//            println("B1")
//            read(ts, T(r("amogus", "x", integer)))
//        }
//    }

//    val h2OJob = buildLowH2OAgent(ts)

//    joinAll(h2OJob)
}

fun buildLowH2OAgent(ts: TupleSpace): Job {
    val h20Threshold = Variable("h20Threshold", 10)

    return CoroutineScope(Dispatchers.IO).launch {
        H2O_Bas(h20Threshold) def {
            read(ts, T(v("h20_detection", "valeur_H2O", string)))
        }
    }
}

fun buildCapteur_H2O(ts: TupleSpace): Job {//
    return CoroutineScope(Dispatchers.IO).launch {
        Capteur_H2O() def {
            add(ts, T(s("niveau-H2O", string), s("valeur-H2O", float)))
                .Capteur_H2O()
        }
    }
}

fun buildCapteur_CH4(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Capteur_CH4() def {
            add(ts, T(s("niveau-CH4", string), s("valeur-CH4", float)))
                .Agent()
        }
    }
}

fun buildCapteur_CO(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Capteur_CO() def {
            add(ts, T(s("niveau-CO", string), s("valeur-CO", float)))
                .Agent()
        }
    }
}

fun buildPompe(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Pompe(etat) def {
            b {
                pop(ts, T(s("activation-pompe", string))).Pompe(activee)
            } + b {
                pop(ts, T(s("désactivation-pompe", string))).Pompe(desactivee)
            }
        }
    }
}

fun buildH2O_haut(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        H2O_Haut(seuil_H2O_haut) def {
            read(ts, T(s("detection-H2O", string)))
                .read(ts, T(s("niveau-H2O", string), r("x", float)))[v("x") >= seuil_H2O_haut] {
                out(ts, T(s("H2O-haut-detecte", string)))
                    .pop(ts, T(s("detection-H2O-haut", string)))
                    .H2O_Haut(seuil_H2O_haut)
            }[v("v") < seuil_H2O_haut] {
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
                        out(ts, T(s("activation-pompe", string)))
                            .out(ts, T(s("detection-H2O-bas", string)))
                            .out(ts, T(s("detection-gaz-haut", string)))
                            .Commande_Pompe_Ventilateur(seuil_CH4, seuil_CO)
                    }
                } + b {
                this[(v("y") >= seuil_CH4) v (v("z") >= seuil_CO)] {
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
                out(ts, T(s("activation-pompe", string)))
                    .out(ts, T(s("detection-H2O-bas", string)))
                    .pop(ts, T(s("detection-gaz-bas", string)))
                    .Gaz_Bas(seuil_CH4, seuil_CO)
            }[(v("x") >= seuil_CH4) v (v("y") >= seuil_CO)] {
                Gaz_Bas(seuil_CH4, seuil_CO)
            }
        }
    }
}

fun buildSurveillance_Gaz_Haut(ts: TupleSpace): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        Surveillance_Gaz_Haut(seuil_CH4, seuil_CO) def {
            read(ts, T(s("detection-gaz-haut", string)))
                .read(ts, T(r("niveau-CO", "x", string)))
                .read(ts, T(r("niveau-CH4", "y", string)))
                .b {
                    this[(v("x") >= seuil_CH4) v (v("y") >= seuil_CO)] {
                        out(ts, T(s("activation-ventilateur", string)))
                            .pop(ts, T(s("detection-gaz-haut", string)))
                            .Surveillance_Gaz_Haut(seuil_CH4, seuil_CO)
                    }
                } + b {
                this[(v("x") < seuil_CH4) `^` (v("y") < seuil_CO)] {
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
                .read(ts, T(r("niveau-H2O", "x", float)))
                .b {
                    this[v("x") < seuil_H2O_bas] {
                        out(ts, T(s("H2O-bas-detecte", string)))
                            .pop(ts, T(s("detection-H2O-bas", string)))
                            .H2O_Bas(seuil_H2O_bas)
                    }
                } + b {
                this[v("x") >= seuil_H2O_bas] {
                    H2O_Bas(seuil_H2O_bas)
                }
            }
        }
    }
}