package org.brewman.strategydemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The CommandStrategyDispatcher will call the correct ICommandStrategy implementation
 * for the given input COMMAND.  Security checks will be performed along the way.
 *
 * The only restriction is that there may be only one ICommandStrategy per COMMAND
 * Type as the COMMAND Type is used as the key to lookup the correct implementation.
 */
@Service
@Slf4j
public class CommandStrategyDispatcher {

    private final Map<Class<?>, ICommandStrategy<?, ?, ?>> handlerMap;

    /**
     * Constructor that takes a List of all the registered Beans that implement the
     * ICommandStrategy.
     *
     * @param handlers the List of registered ICommandStrategies
     */
    public CommandStrategyDispatcher(List<ICommandStrategy<?, ?, ?>> handlers) {
        // Create the Map of COMMAND Types to handlers.
        this.handlerMap = createStrategyMap(handlers);
    }

    /**
     * Build a Map of COMMAND Type to ICommandStrategy handlers.
     *
     * @param handlers
     *
     * @return
     */
    private Map<Class<?>, ICommandStrategy<?, ?, ?>> createStrategyMap(List<ICommandStrategy<?, ?, ?>> handlers) {
        Map<Class<?>, ICommandStrategy<?, ?, ?>> map = new HashMap<>();

        for(ICommandStrategy<?, ?, ?> handler : handlers) {
            Class<?> commandClazz = getClassOfParameterByName(handler.getClass(), "COMMAND");
            log.info("mapping handler of {} to {}", handler.getClass().getName(), commandClazz);
            map.put(commandClazz, handler);
        }

        return Collections.unmodifiableMap(map);
    }

    private Class<?> getClassOfParameterByName(Class<?> handlerClass, String parameterName) {
        Class<?> rtn = null;

        Map<TypeVariable, Type> typeVariableMap = GenericTypeResolver.getTypeVariableMap(handlerClass);
        log.info("typeVariableMap: {}", typeVariableMap);

        TypeVariable key = typeVariableMap.keySet()
                .stream()
                .filter(typeVariable -> typeVariable.getName().equals(parameterName))
                .findFirst()
                .orElse(null);

        log.info("key: {}", key);

        // Seems unlikely.
        if(key == null) {
            throw new AssertionError("handler type variables did not contain " + parameterName);
        }

        Type type = typeVariableMap.get(key);

        log.info("type: {}", type);

        if(type instanceof Class) {
            rtn = (Class<?>)type;
        } else {
            // Also seems unlikely.
            throw new AssertionError("handler parameter type was not a class");
        }

        return rtn;
    }

    public <COMMAND, RESULT, RESOURCE> RESULT dispatch(COMMAND command) {
        log.info("dispatch: {}", command);

        // Get the correct handler and validate that all is setup right.
        ICommandStrategy<COMMAND, RESULT, RESOURCE> handler =
                (ICommandStrategy<COMMAND, RESULT, RESOURCE>)handlerMap.get(command.getClass());
        log.info("handler: {}", handler);

        if(handler == null) {
            // Seems likely.  Should handle this better.
            throw new AssertionError("handler not found");
        }

        // Pre-handle security checks.
        checkPermissions(handler, command);

        // Call the handler and return its result.
        return handler.handle(command);
    }

    /**
     * Perform all security checks required to make sure the user is allowed to do the
     * COMMAND that is about to be performed.
     *
     * @param handler the handler for the specified COMMAND
     * @param command the input COMMAND object
     *
     * @param <COMMAND> the COMMAND Type
     * @param <RESOURCE> the RESOURCE Type
     */
    private <COMMAND, RESOURCE> void checkPermissions(ICommandStrategy<COMMAND, ?, RESOURCE> handler,
                                                      COMMAND command) {
        // Get the resource container that the handler provides for its policy checks.
        RESOURCE resource = handler.getResource(command);

        // Do the policy check.
        // TODO: Assume we have a PolicyEnforcement engine here.
    }
}
