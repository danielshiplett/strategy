package org.brewman.strategydemo.service;

/**
 * Interface to implement a Command handler.
 *
 * @param <COMMAND> the input command Type
 * @param <RESULT> the output result Type
 * @param <RESOURCE> the resource container Type
 */
public interface ICommandStrategy<COMMAND, RESULT, RESOURCE> {

    /**
     * Handle the COMMAND.  This will be called after policy enforcement.  No
     * additional permission checking is needed in this method.
     *
     * @param command the input COMMAND object.
     *
     * @return the final RESULT of the handler.
     */
    RESULT handle(COMMAND command);

    /**
     * Get a filled resource container that can be used for this command's
     * policy enforcement.
     *
     * A NULL RESOURCE is OK if there are no policy checks that
     * are performed for the given command or if the policy doesn't need any
     * resource data to perform the checks.
     *
     * Additionally, this method may return RuntimeExceptions that can be
     * handled by an Exception Advice Translator.  For example, when an input
     * name refers to a resource that does not exist.
     *
     * @param command the input COMMAND object.
     *
     * @return a resource container to be used during policy enforcement.
     */
    RESOURCE getResource(COMMAND command);
}
