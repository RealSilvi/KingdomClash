package it.unibo.model.data;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple data class that stores a type of resource and it's corresponding amount.
 */
public final class Resource implements Serializable, Cloneable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 142745963L;

    /**
     * An enum containing all type of resources.
     */
    public enum ResourceType {
        /**
         * Wheat resource type.
         */
        WHEAT,
        /**
         * Wood resource type.
         */
        WOOD
    }

    private ResourceType resource;
    private int amount;
    /**
     * Constructs a new resource with a given amount and type.
     * @param resource  the type of resource
     * @param amount    the amount of the resource
     */
    public Resource(final ResourceType resource, final int amount) {
        this.resource = resource;
        this.amount = amount;
    }
    /**
     * Constructs a new resource with a given type and 0 amount.
     * @param resource  the type of resource
     */
    public Resource(final ResourceType resource) {
        this(resource, 0);
    }
    /**
     * @return          the type of this resource
     */
    public ResourceType getResource() {
        return resource;
    }
    /**
     * @param resource  the type of this resource
     */
    public void setResource(final ResourceType resource) {
        this.resource = resource;
    }
    /**
     * @return          the amount of this resource
     */
    public int getAmount() {
        return amount;
    }
    /**
     * @param amount    the amount of this resource
     */
    public void setAmount(final int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ResourceType: " + resource.name() + "\nAmount: " + amount;
    }

    @Override
    public int hashCode() {
        return getResource().hashCode();
    }

    @Override
    public boolean equals(final Object otherResource) {
        if (otherResource == null) {
            return false;
        }
        if ((getClass() == otherResource.getClass()) 
            && (this.getResource() == ((Resource) otherResource).getResource())) {
            return true;
        }
        return super.equals(otherResource);
    }
    /**
     * Constructs a copy of this resource.
     * @return a deep copy of this resource
     */
    public Resource clone() {
        return new Resource(this.resource, this.amount);
    }

    /**
     * Performs a deep copy of a given resource set.
     *
     * @param resourceSet the set to be copy
     * @return a deep copy of the given set
     */
    public static Set<Resource> deepCopySet(final Set<Resource> resourceSet) {
        return resourceSet.stream()
                .map(Resource::clone)
                .collect(Collectors.toSet());
    }
    /**
     * Returns a formatted resource set as string.
     * @param resourceSet the set to format
     * @return          a formatted resource set string
     */
    public static String beautifyToString(final Set<Resource> resourceSet) {
        StringBuilder stringBuilder = new StringBuilder();
        resourceSet.stream().forEach(resource ->
            stringBuilder.append(resource.getResource()
                .name().substring(0, 1).toUpperCase())
            .append(resource.getResource().name().substring(1)
            .toLowerCase(Locale.getDefault()))
            .append(": ")
            .append(resource.getAmount())
            .append("\n")
        );
        String beautifiedOutput = stringBuilder.toString();
        return beautifiedOutput.trim();
    }
    /**
     * Checks if all resources listed in the ResourceTypes enum are present in the given set.
     * If any resource is missing, it adds it to the set with a quantity of 0.
     *
     * @param resourceSet the set of resources to be checked and modified
     * @return the modified set of resources with all resource types included
     */
    public static Set<Resource> checkAndAddMissingResources(Set<Resource> resourceSet) {
        EnumSet.allOf(ResourceType.class)
                .stream()
                .filter(resourceType -> resourceSet.stream()
                        .noneMatch(resource -> resource.getResource() == resourceType))
                .map(Resource::new)
                .forEach(resourceSet::add);

        return resourceSet;
    }
}
