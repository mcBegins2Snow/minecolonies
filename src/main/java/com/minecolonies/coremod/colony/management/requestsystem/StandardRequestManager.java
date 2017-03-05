package com.minecolonies.coremod.colony.management.requestsystem;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.minecolonies.blockout.Log;
import com.minecolonies.coremod.colony.IColony;
import com.minecolonies.coremod.colony.management.requestsystem.api.IRequestManager;
import com.minecolonies.coremod.colony.management.requestsystem.api.RequestState;
import com.minecolonies.coremod.colony.management.requestsystem.api.factory.IFactory;
import com.minecolonies.coremod.colony.management.requestsystem.api.factory.IFactoryController;
import com.minecolonies.coremod.colony.management.requestsystem.api.location.ILocatable;
import com.minecolonies.coremod.colony.management.requestsystem.api.request.IRequest;
import com.minecolonies.coremod.colony.management.requestsystem.api.resolver.IRequestResolver;
import com.minecolonies.coremod.colony.management.requestsystem.api.resolver.IRequestResolverProvider;
import com.minecolonies.coremod.colony.management.requestsystem.api.token.IToken;
import com.minecolonies.coremod.colony.management.requestsystem.token.StandardToken;
import com.sun.org.apache.regexp.internal.RE;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marcf on 2/27/2017.
 */
public class StandardRequestManager implements IRequestManager {

    @NotNull
    private final BiMap<IToken, IRequestResolverProvider> providerBiMap = HashBiMap.create();
    
    /**
     * BiMap that holds unique token to resolver lookup.
     */
    @NotNull
    private final BiMap<IToken, IRequestResolver> resolverBiMap = HashBiMap.create();

    /**
     * BiMap that holds unique token to request lookup.
     */
    @NotNull
    private final BiMap<IToken, IRequest> requestBiMap = HashBiMap.create();

    /**
     * Map that holds the resolvers that are linked to a given provider.
     */
    @NotNull
    private final HashMap<IToken, ImmutableCollection<IToken>> providerResolverMap = new HashMap<> ();

    /**
     * Map that holds the requests that are linked to a given resolver.
     */
    @NotNull
    private final HashMap<IToken, Collection<IToken>> resolverRequestMap = new HashMap<>();

    /**
     * Map that holds the resolver that is linked to a given request.
     */
    @NotNull
    private final HashMap<IToken, IToken> requestResolverMap = new HashMap<>();

    /**
     * Map that holds the class that resolver can resolve. Used during lookup.
     */
    @NotNull
    private final HashMap<Class, Collection<IRequestResolver>> requestClassResolverMap = new HashMap<>();

    @NotNull
    private IColony colony;

    /**
     * The colony this manager manages the requests for.
     *
     * @return The colony this manager manages the requests for.
     */
    @NotNull
    @Override
    public IColony getColony() {
        return colony;
    }

    /**
     * Method used to get the FactoryController of the RequestManager.
     *
     * @return The FactoryController of this RequestManager.
     */
    @NotNull
    @Override
    public IFactoryController getFactoryController() {
        return StandardFactoryController.getInstance();
    }

    /**
     * Method to create a request for a given object
     *
     * @param requester The requester.
     * @param object    The Object that is being requested.
     * @return The token representing the request.
     * @throws IllegalArgumentException is thrown when this manager cannot produce a request for the given types.
     */
    @NotNull
    @Override
    public <T> IToken createRequest(@NotNull ILocatable requester, @NotNull T object) throws IllegalArgumentException {
        return null;
    }

    /**
     * Method to get a request for a given token.
     *
     * @param token The token to get a request for.
     * @return The request of the given type for that token.
     * @throws IllegalArgumentException when either their is no request with that token, or the token does not produce a request of the given type T.
     */
    @NotNull
    @Override
    public <T> IRequest<T> getRequestForToken(@NotNull IToken token) throws IllegalArgumentException {
        return null;
    }

    /**
     * Method to update the state of a given request.
     *
     * @param token The token that represents a given request to update.
     * @param state The new state of that request.
     * @throws IllegalArgumentException when the token is unknown to this manager.
     */
    @NotNull
    @Override
    public void updateRequestState(@NotNull IToken token, @NotNull RequestState state) throws IllegalArgumentException {

    }

    /**
     * Method used to indicate to this manager that a new Provider has been added to the colony.
     *
     * @param provider The new provider.
     */
    @Override
    public void onProviderAddedToColony(@NotNull IRequestResolverProvider provider) throws IllegalArgumentException {
        if (providerResolverMap.containsKey(provider.getToken()))
            throw new IllegalArgumentException("The given token: " + provider.getResolvers() + "is already registered.");

        ImmutableList.Builder<IRequestResolver> resolverListBuilder = new ImmutableList.Builder<>();
        resolverListBuilder.addAll(provider.getResolvers());

        providerResolverMap.put(provider.getToken(), resolverListBuilder.build());
    }

    /**
     * Method used to indicate to this manager that Provider has been removed from the colony.
     *
     * @param provider The removed provider.
     */
    @Override
    public void onProviderRemovedFromColony(@NotNull IRequestResolverProvider provider) throws IllegalArgumentException {
        List<IToken> providerRequests =
    }

    /**
     * Method used to serialize the current request system to NBT.
     * @return The NBTData that describes the current request system
     */
    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    /**
     * Method used to deserialize the data inside the given nbt tag into this request system.
     * @param nbt The data to deserialize.
     */
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }

    /**
     * Class used to handle the inner workings of the request system with regards to providers.
     */
    private final static class ProviderHandler {

        /**
         * Method used to get a provider from a token.
         * @param token The token to get the provider form.
         * @return The provider that corresponds to the given token
         * @throws IllegalArgumentException when no provider is not registered with the given token.
         */
        private static IRequestResolverProvider getProvider(StandardRequestManager manager, IToken token) throws IllegalArgumentException {
            if (!manager.providerBiMap.containsKey(token))
                throw new IllegalArgumentException("The given token for a provider is not registered");

            return manager.providerBiMap.get(token);
        }

        /**
         * Method used to get the registered resolvers for a given provider.
         * @param manager The manager to pull the data from.
         * @param token The token of the provider you are requesting the resolvers for.
         * @return The registered resolvers that belong to the given provider.
         * @throws IllegalArgumentException when the token is not belonging to a registered provider.
         */
        private static ImmutableCollection<IToken> getRegisteredResolvers(StandardRequestManager manager, IToken token) throws IllegalArgumentException {
            //Check if the token is registered.
            getProvider(manager, token);

            return manager.providerResolverMap.get(token);
        }

        /**
         * Method used to get the registered resolvers for a given provider.
         * @param manager The manager to pull the data from.
         * @param provider The provider you are requesting the resolvers for.
         * @return The registered resolvers that belong to the given provider.
         * @throws IllegalArgumentException when the token is not belonging to a registered provider.
         */
        private static ImmutableCollection<IToken> getRegisteredResolvers(StandardRequestManager manager, IRequestResolverProvider provider) throws IllegalArgumentException {
            //Check if the token is registered.
            getProvider(manager, provider.getToken());

            return manager.providerResolverMap.get(provider.getToken());
        }

        /**
         * Method used to register a provider to a given manager.
         * @param manager The manager to register them to.
         * @param provider The provider that provides the resolvers.
         * @throws IllegalArgumentException is thrown when a provider is already registered.
         */
        private static void registerProvider(StandardRequestManager manager, IRequestResolverProvider provider) throws IllegalArgumentException {
            if (manager.providerBiMap.containsKey(provider.getToken()) ||
                    manager.providerBiMap.containsValue(provider))
                throw new IllegalArgumentException("The given provider is already registered");

            manager.providerBiMap.put(provider.getToken(), provider);

            ImmutableList.Builder<IToken> resolverListBuilder = new ImmutableList.Builder<>();
            resolverListBuilder.addAll(ResolverHandler.registerResolvers(manager, provider.getResolvers()));

            manager.providerResolverMap.put(provider.getToken(), resolverListBuilder.build());
        }

        private static void removeProvider(StandardRequestManager manager, IToken token) throws IllegalArgumentException {
            removeProviderInternal(manager, token);
        }

        private static void removeProvider(StandardRequestManager manager, IRequestResolverProvider provider) throws IllegalArgumentException {
            IRequestResolverProvider registeredProvider = getProvider(manager, provider.getToken());

            if (!registeredProvider.equals(provider))
                throw new IllegalArgumentException("The given providers token is registered to a different provider!");

            removeProviderInternal(manager, provider.getToken());
        }

        /**
         * Internal method that handles the reassignment
         * @param manager The manager that is being modified.
         * @param token The token of the provider that is being removed.
         * @throws IllegalArgumentException is thrown when the token is not registered to a provider, or when the data stored in the manager is in conflict.
         */
        private static void removeProviderInternal(StandardRequestManager manager, IToken token) throws IllegalArgumentException {
            IRequestResolverProvider provider = getProvider(manager, token);

            Log.getLogger().debug("Removing provider: " + provider);

            //Get the resolvers that are being removed.
            ImmutableCollection<IToken> assignedResolvers = getRegisteredResolvers(manager, token);
            for(IToken resolverToken : assignedResolvers) {
                //If no requests are assigned to this resolver skip.
                if (!manager.resolverRequestMap.containsKey(resolverToken))
                    continue;

                //Skip if the resolver has no requests assigned.
                if (manager.resolverRequestMap.get(resolverToken).size() == 0) {
                    Log.getLogger().debug("Removing resolver without assigned requests: " + resolverToken);
                    manager.resolverRequestMap.remove(resolverToken);

                    ResolverHandler.removeResolver(manager, resolverToken);

                    continue;
                }

                //Clone the original list to modify it during iteration, if need be.
                Collection<IToken> assignedRequests = new ArrayList<>(manager.resolverRequestMap.get(resolverToken));
                Log.getLogger().debug("Starting reassignment of already registered requests registered to resolver with token: " + resolverToken);

                //Get all assigned requests and reassign them.
                for(IToken requestToken : assignedRequests) {
                    Log.getLogger().debug("Removing assigned request: " + requestToken + " from resolver: " + resolverToken);

                    //No need to notify the resolver of the cancellation, It is getting removed anyway.
                    //In that case: All resources lost, restart on different resolver.
                    //Also cancel all registered child task:
                    manager.resolverRequestMap.get(resolverToken).remove(requestToken);
                    manager.requestResolverMap.remove(requestToken);

                    Log.getLogger().debug("Cancelling all child requests of:" + requestToken);

                    //Check if the request has children.
                    IRequest assignedRequest = RequestHandler.getRequest(manager, requestToken);
                    if (assignedRequest.hasChildren()) {
                        //Iterate over all children and call there onParentCancelled method to get a new cleanup parent.
                        for (Object objectToken :
                                assignedRequest.getChildren()) {
                            if (objectToken instanceof IToken) {
                                IToken childToken = (IToken) objectToken;
                                IRequest childRequest = RequestHandler.getRequest(manager, childToken);

                                //Check if the child has been assigned. If not, no work done, no cleanup needed.
                                if (RequestHandler.isAssigned(manager, childToken)) {
                                    //Get the child request
                                    IRequestResolver childResolver = ResolverHandler.getResolverForRequest(manager, childToken);
                                    IRequest cleanUpRequest = childResolver.onParentCancelled(manager, childRequest);

                                    //Switch out the parent, and add the old child to the followup request as new child
                                    cleanUpRequest.addChild(childToken);
                                    childRequest.setParent(cleanUpRequest.getToken());

                                    //Assign the new followup request if it is not assigned yet.
                                    if(!RequestHandler.isAssigned(manager, cleanUpRequest.getToken()))
                                        RequestHandler.assignRequest(manager, cleanUpRequest);
                                }
                            }
                        }
                    }

                    Log.getLogger().debug("Starting reassignment of: " + requestToken + " - Assigned to: " + resolverToken);

                    RequestHandler.assignRequest(manager, assignedRequest, assignedResolvers);

                    Log.getLogger().debug("Finished reassignment of: " + requestToken + " - Assigned to: " + manager.requestResolverMap.get(requestToken));
                }

                ResolverHandler.removeResolver(manager, resolverToken);

                Log.getLogger().debug("Finished reassignment of already registered requests registered to resolver with token: " + resolverToken);
            }

            //Removing the data from the maps.
            manager.providerBiMap.remove(provider.getToken());
            manager.providerResolverMap.remove(provider.getToken());

            Log.getLogger().debug("Removed provider: " + provider);
        }
    }

    /**
     * Class used to handle the inner workings of the request system with regards to resolvers.
     */
    private final static class ResolverHandler {

        /**
         * Method to get a resolver from a given token.
         *
         * <p>
         *     Is only used internally.
         *     Querries the resolverBiMap to get the resolver for a given Token.
         * </p>
         *
         * @param manager The manager to retrieve the resolver from.
         * @param token The token of the resolver to look up.
         * @return The resolver registered with the given token.
         * @throws IllegalArgumentException is thrown when the given token is not registered to any IRequestResolver
         */
        private static IRequestResolver getResolver(StandardRequestManager manager, IToken token) throws IllegalArgumentException {
            if (!manager.resolverBiMap.containsKey(token))
                throw new IllegalArgumentException("The given token for a resolver is not known to this manager!");

            Log.getLogger().debug("Retrieving resolver for: " + token);

            return manager.resolverBiMap.get(token);
        }

        /**
         * Method used to register a resolver.
         *
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverBiMap that is used to track which resolver are registered.
         * </p>
         *
         * @param manager The manager to register the resolver to.
         * @param resolver The resolver to register
         * @return The token of the newly registered resolver
         * @throws IllegalArgumentException is thrown when either the token attached to the resolver is already registered or the resolver is already registered with a different token
         */
        private static IToken registerResolver(StandardRequestManager manager, IRequestResolver resolver) throws IllegalArgumentException {
            if (manager.resolverBiMap.containsKey(resolver.getToken()))
                throw new IllegalArgumentException("The token attached to this resolver is already registered. Cannot register twice!");

            if (manager.resolverBiMap.containsValue(resolver))
                throw new IllegalArgumentException("The given resolver is already registered with a different token. Cannot register twice!");

            Log.getLogger().debug("Registering resolver: " + resolver + " with request type: " + resolver.getRequestType().getName());

            manager.resolverBiMap.put(resolver.getToken(), resolver);

            if (!manager.requestClassResolverMap.containsKey(resolver.getRequestType()))
                manager.requestClassResolverMap.put(resolver.getRequestType(), new ArrayList<>());

            manager.requestClassResolverMap.get(resolver.getRequestType()).add(resolver);

            return resolver.getToken();
        }

        /**
         * Method used to register multiple resolvers simultaneously
         *
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverBiMap that is used to track resolvers.
         * </p>
         * 
         * @param manager The manager to register the resolvers to.
         * @param resolvers The resolvers to register.
         * @return The tokens of the resolvers that when registered.
         * @throws IllegalArgumentException is thrown when an IllegalArgumentException is thrown by the registerResolver method for any of the given Resolvers.
         */
        private static Collection<IToken> registerResolvers(StandardRequestManager manager, IRequestResolver... resolvers) throws IllegalArgumentException {
            return Arrays.stream(resolvers).map(resolver -> registerResolver(manager, resolver)).collect(Collectors.toList());
        }

        /**
         * Method used to register multiple resolvers simultaneously
         *
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverBiMap that is used to track resolvers.
         * </p>
         *
         * @param manager The manager to register the resolvers to.
         * @param resolvers The resolvers to register.
         * @return The tokens of the resolvers that when registered.
         * @throws IllegalArgumentException is thrown when an IllegalArgumentException is thrown by the registerResolver method for any of the given Resolvers.
         */
        private static Collection<IToken> registerResolvers(StandardRequestManager manager, Collection<IRequestResolver> resolvers) {
            return resolvers.stream().map(resolver -> registerResolver(manager, resolver)).collect(Collectors.toList());
        }

        /**
         * Method used to remove a registered resolver.
         * 
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverBiMap that is used to track resolvers.
         * </p>
         * 
         * @param manager The manager to remove the resolver from.
         * @param resolver The resolver to remove
         * @throws IllegalArgumentException is thrown when the given resolver is not registered or the token of the given resolver is not registered to the same resolver.
         */
        private static void removeResolver(StandardRequestManager manager, IRequestResolver resolver) throws IllegalArgumentException {
            IRequestResolver registeredResolver = getResolver(manager, resolver.getToken());

            if (!registeredResolver.equals(resolver))
                throw new IllegalArgumentException("The given resolver and the resolver that is registered with its token are not the same.");

            if (manager.resolverRequestMap.containsKey(registeredResolver.getToken()) && manager.resolverRequestMap.get(registeredResolver.getToken()).size() > 0)
                throw new IllegalArgumentException("Cannot remove a resolver that is still in use. Reassign all registered requests before removing");

            Log.getLogger().debug("Removing resolver: " + resolver + " with request type: " + resolver.getRequestType().getName());

            manager.resolverBiMap.remove(resolver.getToken());
            manager.requestClassResolverMap.get(resolver.getRequestType()).remove(resolver);
        }

        /**
         * Method used to remove a registered resolver.
         *
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverBiMap that is used to track resolvers.
         * </p>
         *
         * @param manager The manager to remove the resolver from.
         * @param token The token of the resolver to remove.
         * @throws IllegalArgumentException is thrown when the given resolver is not registered or the token of the given resolver is not registered to the same resolver.
         */
        private static void removeResolver(StandardRequestManager manager, IToken token) throws IllegalArgumentException {
            if (!manager.resolverBiMap.containsKey(token))
                throw new IllegalArgumentException("The token is unknown to this manager.");

            removeResolver(manager, getResolver(manager, token));
        }

        /**
         * Method used to remove a multiple registered resolvers.
         *
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverBiMap that is used to track resolvers.
         * </p>
         *
         * @param manager The manager to remove the resolver from.
         * @param resolvers The resolvers to remove.
         * @throws IllegalArgumentException is thrown when removeResolver throws an IllegalArgumentException for any of the given resolvers.
         */
        private static void removeResolvers(StandardRequestManager manager, IRequestResolver... resolvers) {
            removeResolvers(manager, Arrays.asList(resolvers));
        }

        /**
         * Method used to remove a multiple registered resolvers.
         *
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverBiMap that is used to track resolvers.
         * </p>
         *
         * @param manager The manager to remove the resolver from.
         * @param resolvers The resolvers to remove.
         * @throws IllegalArgumentException is thrown when removeResolver throws an IllegalArgumentException for any of the given resolvers.
         */
        private static void removeResolvers(StandardRequestManager manager, Iterable<IRequestResolver> resolvers) {
            resolvers.forEach((IRequestResolver resolver) -> {
                removeResolver(manager, resolver);
            });
        }

        /**
         * Method used to add a request to a resolver.
         *
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverRequestMap that is used to track which resolver handles which request.
         * </p>
         *
         * @param manager The manager to modify
         * @param resolver The resolver to add the request to.
         * @param request The request to add to the resolver.
         */
        private static void addRequestToResolver(StandardRequestManager manager, IRequestResolver resolver, IRequest request) {
            if (!manager.resolverRequestMap.containsKey(resolver.getToken()))
                manager.resolverRequestMap.put(resolver.getToken(), new ArrayList<>());

            Log.getLogger().debug("Adding request: " + request + " to resolver: " + resolver);

            manager.resolverRequestMap.get(resolver.getToken()).add(request.getToken());
            manager.requestResolverMap.put(request.getToken(), resolver.getToken());
        }

        /**
         * Method used to remove a request from a resolver.
         *
         * <p>
         *     Is only used internally.
         *     The method modifies the resolverRequestMap that is used to track which resolver handles which request.
         * </p>
         *
         * @param manager The manager to modify
         * @param resolver The resolver to remove the given request from.
         * @param request The request to remove.
         * @throws IllegalArgumentException is thrown when the resolver is unknown, or when the given request is not registered to the given resolver.
         */
        private static void removeRequestFromResolver(StandardRequestManager manager, IRequestResolver resolver, IRequest request) throws IllegalArgumentException {
            if (!manager.resolverRequestMap.containsKey(resolver.getToken()))
                throw new IllegalArgumentException("The given resolver is unknown to this Manager");

            if (!manager.resolverRequestMap.get(resolver.getToken()).contains(request.getToken()))
                throw new IllegalArgumentException("The given request is not registered to the given resolver.");

            Log.getLogger().debug("Removing request: " + request + " from resolver: " + resolver);

            manager.resolverRequestMap.get(resolver.getToken()).remove(request.getToken());
            manager.requestResolverMap.remove(request.getToken());

        }

        /**
         * Method used to get a resolver from a given request token.
         * @param manager The manager to get the resolver from.
         * @param requestToken The token of a request a the assigned resolver is requested for.
         * @return The resolver for the request with the given token.
         * @throws IllegalArgumentException when the token is unknown or the request is not assigned yet.
         */
        private static IRequestResolver getResolverForRequest(StandardRequestManager manager, IToken requestToken) throws IllegalArgumentException {
            RequestHandler.getRequest(manager, requestToken);

            if (!manager.requestResolverMap.containsKey(requestToken))
                throw new IllegalArgumentException("The given token belongs to a not resolved request");

            return getResolver(manager, manager.requestResolverMap.get(requestToken));
        }

        /**
         * Method used to get a resolver from a given request token.
         * @param manager The manager to get the resolver from.
         * @param request The request a the assigned resolver is requested for.
         * @return The resolver for the request.
         * @throws IllegalArgumentException when the token is unknown or the request is not assigned yet.
         */
        private static IRequestResolver getResolverForRequest(StandardRequestManager manager, IRequest request) {
            RequestHandler.getRequest(manager, request.getToken());

            if (!manager.requestResolverMap.containsKey(request.getToken()))
                throw new IllegalArgumentException("The given token belongs to a not resolved request");

            return getResolver(manager, manager.requestResolverMap.get(request.getToken()));
        }
    }

    /**
     * Class used to handle the inner workings of the request system with regards to requests.
     */
    private final static class RequestHandler {

        /**
         * Method used to get a registered request from a given token.
         * @param token The token to query
         * @throws IllegalArgumentException when the token is unknown to the given manager.
         */
        private static IRequest getRequest(StandardRequestManager manager, IToken token) throws IllegalArgumentException {
            if (!manager.requestBiMap.containsKey(token))
                throw new IllegalArgumentException("The given token is not registered as a request to this manager");

            Log.getLogger().debug("Retrieving the request for: " + token);

            return manager.requestBiMap.get(token);
        }

        @SuppressWarnings("unchecked")
        private static <Request> IRequest<Request> createRequest(StandardRequestManager manager, Request request) {
            Class requestClass = request.getClass();

            IToken token = TokenHandler.generateNewToken(manager);

            IFactory<Request, ?> factory = manager.getFactoryController().getFactoryForInput(requestClass);
            IRequest<Request> constructedRequest = (IRequest<Request>) factory.getNewInstance(request, token);

            Log.getLogger().debug("Creating request for: " + request + " with factory: " + factory + ", token: " + token + " and output: " + constructedRequest);

            registerRequest(manager, constructedRequest);

            return constructedRequest;
        }

        private static void registerRequest(StandardRequestManager manager, IRequest request) throws IllegalArgumentException {
            if (manager.requestBiMap.containsKey(request.getToken()) ||
                    manager.requestBiMap.containsValue(request))
                throw new IllegalArgumentException("The given request is already known to this manager");

            Log.getLogger().debug("Registering request: " + request);

            manager.requestBiMap.put(request.getToken(), request);
        }

        /**
         * Method used to assign a given request to a resolver. Does not take any blacklist into account.
         * @param manager The manager to modify.
         * @param request The request to assign
         * @throws IllegalArgumentException when the request is already assigned
         */
        private static void assignRequest(StandardRequestManager manager, IRequest request) throws IllegalArgumentException {
            assignRequest(manager, request, Collections.EMPTY_LIST);
        }

        /**
         * Method used to assign a given request to a resolver. Does take a given blacklist of resolvers into account.
         * @param manager The manager to modify.
         * @param request The request to assign.
         * @param resolverTokenBlackList Each resolver that has its token in this blacklist will be skipped when checking for a possible resolver.
         * @throws IllegalArgumentException is thrown when the request is unknown to this manager.
         */
        private static void assignRequest(StandardRequestManager manager, IRequest request, IToken... resolverTokenBlackList) throws IllegalArgumentException {
            assignRequest(manager, request, Arrays.asList(resolverTokenBlackList));
        }

        /**
         * Method used to assign a given request to a resolver. Does take a given blacklist of resolvers into account.
         * @param manager The manager to modify.
         * @param request The request to assign.
         * @param resolverTokenBlackList Each resolver that has its token in this blacklist will be skipped when checking for a possible resolver.
         * @throws IllegalArgumentException is thrown when the request is unknown to this manager.
         */
        private static void assignRequest(StandardRequestManager manager, IRequest request, Collection<IToken> resolverTokenBlackList) throws IllegalArgumentException {
            //Check if the request is registered
            getRequest(manager, request.getToken());

            Log.getLogger().debug("Starting resolver assignment search for request: " + request);

            for(IRequestResolver resolver : manager.requestClassResolverMap.get(request.getRequestType())) {
                //Skip when the resolver is in the blacklist.
                if (resolverTokenBlackList.contains(resolver.getToken()))
                    continue;

                //Skip if preliminary check fails
                if (!resolver.canResolve(manager, request))
                    continue;

                @Nullable List<IRequest> attemptResult = resolver.attemptResolve(new WrappedBlacklistAssignmentRequestManager(manager, resolverTokenBlackList), request);

                //Skip if attempt failed (aka attemptResult == null)
                if (attemptResult == null)
                    continue;

                //Succesfully found a resolver. Registering
                Log.getLogger().debug("Finished resolver assignment search for request: " + request + " successfully");
                ResolverHandler.addRequestToResolver(manager, resolver, request);

                //TODO: Call can resolveImmediatly and process result.

                //Change the data in the request and resolver maps.
                manager.requestResolverMap.put(request.getToken(), resolver.getToken());
                if (!manager.resolverRequestMap.containsKey(resolver.getToken()))
                    manager.resolverRequestMap.put(resolver.getToken(), new ArrayList<>());

                manager.resolverRequestMap.get(resolver.getToken()).add(request.getToken());


                //We have found a resolver abort further processing.
                return;
            }

            //TODO: Implement player fallback!
        }

        /**
         * Method used to check if a given request token is assigned to a resolver.
         * @param manager The manager to check for.
         * @param token The request token to check for.
         * @return True when the request token has been assigned, false when not.
         */
        private static boolean isAssigned(StandardRequestManager manager, IToken token) {
            return manager.requestResolverMap.containsKey(token);
        }

        /**
         * Method used to handle the successful resolving of a request.
         *
         * @param manager The manager that got notified of the successful resolving of the request.
         * @param token The token of the request that got finished successfully.
         */
        private static void onRequestSuccessful(StandardRequestManager manager, IToken token) {
            IRequest request = getRequest(manager, token);
            IRequestResolver resolver = ResolverHandler.getResolverForRequest(manager, token);

            //Retrieve a followup request.
            IRequest followupRequest = resolver.getFollowupRequestForCompletion(manager, request);

            //Check if the request has a parent
            if (request.hasParent()) {
                IRequest parentRequest = getRequest(manager, request.getToken());

                //Assign the followup to the parent as a child so that processing is still halted.
                if (followupRequest != null)
                    parentRequest.addChild(followupRequest.getToken());

                parentRequest.removeChild(request.getToken());
                //TODO: Check for completion of all child tasks. If so run parent.
            }

            //Assign the followup request if need be
            if (followupRequest != null) {
                if (!isAssigned(manager, followupRequest.getToken()))
                    assignRequest(manager, followupRequest);
            }
        }

        /**
         * Method used to handle request that were cancelled or overruled.
         * @param manager The manager that got notified of the cancellation or overruling.
         * @param token The token of the request that got cancelled or overruled
         */
        private static void onRequestCancelled(StandardRequestManager manager, IToken token) {
            IRequest request = getRequest(manager, token);
            IRequestResolver resolver = ResolverHandler.getResolverForRequest(manager, token);

            IRequest cleanUpRequest = resolver.onParentCancelled(manager, request);

            if (cleanUpRequest != null) {
                //Switch out the parent, and add the old child to the cleanup request as new child
                cleanUpRequest.addChild(token);
                request.setParent(cleanUpRequest.getToken());

                //Assign the new followup request if it is not assigned yet.
                if(!RequestHandler.isAssigned(manager, cleanUpRequest.getToken()))
                    RequestHandler.assignRequest(manager, cleanUpRequest);
            }
        }
    }

    /**
     * Class used to handle the inner workings of the request system with regards to tokens.
     */
    private final static class TokenHandler {

        private static IToken generateNewToken(StandardRequestManager manager) {
            //Force generic type to be correct.
            StandardToken standardToken = manager.getFactoryController().getNewInstance(UUID.randomUUID());
            return standardToken;
        }
    }

    private final static class WrappedBlacklistAssignmentRequestManager implements IRequestManager {

        @NotNull
        private final StandardRequestManager wrappedManager;
        @NotNull
        private final Collection<IToken> blackListedResolvers;

        private WrappedBlacklistAssignmentRequestManager(@NotNull StandardRequestManager wrappedManager, @NotNull Collection<IToken> blackListedResolvers) {
            this.wrappedManager = wrappedManager;
            this.blackListedResolvers = blackListedResolvers;
        }

        /**
         * The colony this manager manages the requests for.
         *
         * @return The colony this manager manages the requests for.
         */
        @NotNull
        @Override
        public IColony getColony() {
            return wrappedManager.getColony();
        }

        /**
         * Method used to get the FactoryController of the RequestManager.
         *
         * @return The FactoryController of this RequestManager.
         */
        @NotNull
        @Override
        public IFactoryController getFactoryController() {
            return wrappedManager.getFactoryController();
        }

        /**
         * Method to create a request for a given object
         *
         * @param requester The requester.
         * @param object    The Object that is being requested.
         * @return The token representing the request.
         * @throws IllegalArgumentException is thrown when this manager cannot produce a request for the given types.
         */
        @NotNull
        @Override
        public <T> IToken createRequest(@NotNull ILocatable requester, @NotNull T object) throws IllegalArgumentException {
            return wrappedManager.createRequest(requester, object);
        }

        /**
         * Method used to assign a request to a resolver.
         *
         * @param token The token of the request to assign.
         * @throws IllegalArgumentException when the token is not registered to a request, or is already assigned to a resolver.
         */
        @NotNull
        @Override
        public void assignRequest(@NotNull IToken token) throws IllegalArgumentException {
            RequestHandler.assignRequest(wrappedManager, RequestHandler.getRequest(wrappedManager, token), blackListedResolvers);
        }

        /**
         * Method used to create and immediately assign a request.
         *
         * @param requester The requester of the requestable.
         * @param object    The requestable
         * @return The token that represents the request.
         * @throws IllegalArgumentException when either createRequest or assignRequest have thrown an IllegalArgumentException
         */
        @NotNull
        @Override
        public <T> IToken createAndAssignRequest(@NotNull ILocatable requester, @NotNull T object) throws IllegalArgumentException {
            IToken token = createRequest(requester, object);
            assignRequest(token);
            return token;
        }

        /**
         * Method to get a request for a given token.
         *
         * @param token The token to get a request for.
         * @return The request of the given type for that token.
         * @throws IllegalArgumentException when either their is no request with that token, or the token does not produce a request of the given type T.
         */
        @NotNull
        @Override
        public <T> IRequest<T> getRequestForToken(@NotNull IToken token) throws IllegalArgumentException {
            return wrappedManager.getRequestForToken(token);
        }

        /**
         * Method to update the state of a given request.
         *
         * @param token The token that represents a given request to update.
         * @param state The new state of that request.
         * @throws IllegalArgumentException when the token is unknown to this manager.
         */
        @NotNull
        @Override
        public void updateRequestState(@NotNull IToken token, @NotNull RequestState state) throws IllegalArgumentException {
            wrappedManager.updateRequestState(token, state);
        }

        /**
         * Method used to indicate to this manager that a new Provider has been added to the colony.
         *
         * @param provider The new provider.
         * @throws IllegalArgumentException is thrown when a provider with the same token is already registered.
         */
        @Override
        public void onProviderAddedToColony(@NotNull IRequestResolverProvider provider) throws IllegalArgumentException {
            wrappedManager.onProviderAddedToColony(provider);
        }

        /**
         * Method used to indicate to this manager that Provider has been removed from the colony.
         *
         * @param provider The removed provider.
         * @throws IllegalArgumentException is thrown when no provider with the same token is registered.
         */
        @Override
        public void onProviderRemovedFromColony(@NotNull IRequestResolverProvider provider) throws IllegalArgumentException {
            wrappedManager.onProviderRemovedFromColony(provider);
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return wrappedManager.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            wrappedManager.deserializeNBT(nbt);
        }
    }
}
