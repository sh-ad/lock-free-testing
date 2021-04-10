package ru.hse.egorov


import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingCTest
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest
import org.jetbrains.kotlinx.lincheck.verifier.SerializabilityVerifier
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.jetbrains.kotlinx.lincheck.verifier.linearizability.LinearizabilityVerifier
import org.jetbrains.kotlinx.lincheck.verifier.quiescent.QuiescentConsistencyVerifier
import org.junit.Test

@Param(name = "key", gen = IntGen::class)
//@StressCTest(requireStateEquivalenceImplCheck = false)
//@ModelCheckingCTest(requireStateEquivalenceImplCheck = false, minimizeFailedScenario = false, actorsBefore = 10, actorsAfter = 10, actorsPerThread = 100, threads = 3, iterations = 5)
//@ModelCheckingCTest(requireStateEquivalenceImplCheck = false, actorsBefore = 10, actorsAfter = 10, actorsPerThread = 100, threads = 3, iterations = 5)
//@ModelCheckingCTest(iterations = 50, invocationsPerIteration = 1000)
//@ModelCheckingCTest(verifier = SerializabilityVerifier::class, threads = 3, iterations = 5, actorsPerThread = 5)
//@ModelCheckingCTest(verifier = QuiescentConsistencyVerifier::class)
@StressCTest(verifier = QuiescentConsistencyVerifier::class)
//@StressCTest(verifier = SerializabilityVerifier::class)
class LincheckLockFreeSetTest : VerifierState() {
//class LincheckLockFreeSetTest {
    private val set = LockFreeSet<Int>()

    @Operation
    fun add(@Param(name = "key") key: Int) = set.add(key)

    @Operation
    fun remove(@Param(name = "key") key: Int) = set.remove(key)

    @Operation
    fun contains(@Param(name = "key") key: Int) = set.contains(key)

    @Operation
    fun isEmpty() = set.isEmpty

    @Operation
    fun iterator(): List<Int> = set.iterator().asSequence().toList()

    @Test
    fun test() {
        LinChecker.check(LincheckLockFreeSetTest::class.java)
    }

    //State который мы будем использовать для сравнения
    override fun extractState(): Any {
        val elements = mutableListOf<Int>()
        val iterator = set.iterator();
        while (iterator.hasNext()) {
            elements.add(iterator.next())
        }
        return elements
    }
}