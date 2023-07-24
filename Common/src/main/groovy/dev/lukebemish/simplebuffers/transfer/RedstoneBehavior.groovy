package dev.lukebemish.simplebuffers.transfer

import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.util.function.Predicate

@CompileStatic
enum RedstoneBehavior {
    IGNORED({ TransferContext context ->
        return true
    }),
    HIGH({ TransferContext context ->
        return context.level.getSignal(context.blockPos, context.side) !== 0
    }),
    LOW({ TransferContext context ->
        return context.level.getSignal(context.blockPos, context.side) === 0
    })

    private final Predicate<TransferContext> predicate

    RedstoneBehavior(
        @ClosureParams(value = SimpleType, options = 'dev.lukebemish.simplebuffers.transfer.TransferContext')
            Closure<Boolean> predicate) {
        this.predicate = predicate as Predicate<TransferContext>
    }

    boolean test(TransferContext context) {
        return predicate.test(context)
    }
}
