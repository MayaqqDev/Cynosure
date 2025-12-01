package dev.mayaqq.cynosure.events.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.mayaqq.cynosure.events.api.CancellableEvent
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider

public typealias LiteralCommandBuilder = CommandBuilder<LiteralArgumentBuilder<CommandSourceStack>>
public typealias ArgumentCommandBuilder<T> = CommandBuilder<RequiredArgumentBuilder<CommandSourceStack, T>>

/**
 * Fired right before a command executes, cancelling this event prevents command execution
 *
 * @property parseResults allows changing the command's parse results
 * @property exception exception to be thrown if the event is cancelled
 */
public class CommandExecuteEvent(
    public var parseResults: ParseResults<CommandSourceStack>,
    public var exception: Throwable?
) : CancellableEvent()

/**
 * Fired during command registration, allows you to register custom commands
 */
public class CommandRegistrationEvent(
    public val dispatcher: CommandDispatcher<CommandSourceStack>,
    public val context: CommandBuildContext,
    public val selection: Commands.CommandSelection
) : Event {
    public fun register(command: LiteralArgumentBuilder<CommandSourceStack>) {
        dispatcher.register(command)
    }

    public fun register(command: String, builder: LiteralCommandBuilder.() -> Unit) {
        if (command.contains(" ")) {
            LiteralArgumentBuilder.literal<CommandSourceStack>(command.substringBefore(" "))
                ?.apply {
                    LiteralCommandBuilder(this).then(command.substringAfter(" "), action = builder)
                    .let { dispatcher::register }
            }
            return
        }

        LiteralArgumentBuilder.literal<CommandSourceStack>(command)
            ?.apply { LiteralCommandBuilder(this).apply(builder) }
            ?.let(dispatcher::register)
    }
}

public class CommandBuilder<B : ArgumentBuilder<CommandSourceStack, B>> internal constructor(
    private val builder: ArgumentBuilder<CommandSourceStack, B>,
) {

    public fun callback(callback: CommandContext<CommandSourceStack>.() -> Unit) {
        this.builder.executes {
            callback(it)
            1
        }
    }

    public fun then(vararg names: String, action: LiteralCommandBuilder.() -> Unit): CommandBuilder<B> {
        for (name in names) {
            if (name.contains(" ")) {
                val builder = CommandBuilder(LiteralArgumentBuilder.literal(name.substringBefore(" ")))
                builder.then(name.substringAfter(" "), action = action)
                this.builder.then(builder.builder)
                continue
            }
            val builder = CommandBuilder(LiteralArgumentBuilder.literal(name))
            builder.action()
            this.builder.then(builder.builder)
        }
        return this
    }

    public fun <T> then(
        name: String,
        argument: ArgumentType<T>,
        suggestions: Collection<String>,
        action: ArgumentCommandBuilder<T>.() -> Unit,
    ): CommandBuilder<B> = then(
        name,
        argument,
        { _, builder -> SharedSuggestionProvider.suggest(suggestions, builder) },
        action,
    )

    public fun <T> then(
        name: String,
        argument: ArgumentType<T>,
        suggestions: SuggestionProvider<CommandSourceStack>? = null,
        action: ArgumentCommandBuilder<T>.() -> Unit,
    ): CommandBuilder<B> {
        if (name.contains(" ")) {
            val builder = CommandBuilder(LiteralArgumentBuilder.literal(name.substringBefore(" ")))
            builder.then(name.substringAfter(" "), argument, suggestions, action)
            this.builder.then(builder.builder)
            return this
        }
        val builder = CommandBuilder(
            RequiredArgumentBuilder.argument<CommandSourceStack, T>(name, argument).apply {
                if (suggestions != null) suggests(suggestions)
            },
        )
        builder.action()
        this.builder.then(builder.builder)
        return this
    }

    public fun thenCallback(vararg names: String, block: CommandContext<CommandSourceStack>.() -> Unit): CommandBuilder<B> {
        return then(*names) {
            this.callback(block)
        }
    }

    public fun <T> thenCallback(
        name: String,
        argument: ArgumentType<T>,
        suggestions: Collection<String>,
        block: CommandContext<CommandSourceStack>.() -> Unit,
    ): CommandBuilder<B> = then(name, argument, suggestions) {
        this.callback(block)
    }


    public fun <T> thenCallback(
        name: String,
        argument: ArgumentType<T>,
        suggestions: SuggestionProvider<CommandSourceStack>? = null,
        block: CommandContext<CommandSourceStack>.() -> Unit,
    ): CommandBuilder<B> = then(name, argument, suggestions) {
        this.callback(block)
    }
}