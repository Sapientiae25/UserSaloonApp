package com.example.usersaloon

import java.util.stream.Collectors

fun main(){

    val res = arrayListOf("tester1", "tester2", "solverC1", "solverC2", "solverS2", "solverS1", "tester1", "tester4", "system")

    val reuslts: Map<String, Long> = res.stream().collect(
        Collectors.groupingBy(
            { s ->
                s.replace("\\d", "").toString()
//                s.replaceAll("\\d", "").toString()
            }
            ,
            Collectors.counting()
        )
    )

    val sb = StringBuilder()
    val iter = reuslts.entries.iterator()
    while (iter.hasNext()) {
        val (key, value) = iter.next()
        sb.append(key)
        sb.append('=').append('"')
        sb.append(value)
        sb.append('"')
        if (iter.hasNext()) {
            sb.append(',').append(' ')
        }
    }
    println(sb.toString())
}