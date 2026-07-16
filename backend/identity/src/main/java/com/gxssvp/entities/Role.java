package com.gxssvp.entities;

/**
 * Represents the hierarchical structure of access control within the system.
 * Each role defines a distinct level of operational authority and privileges,
 * determining what actions an entity is permitted to perform within the platform.
 *
 * @author Michael Terletskyi
 */
public enum Role {

    /**
     * Role with typical user capabilities - can post, reply, upload content and put likes/dislikes.
     */
    USER,

    /**
     * Role with moderation capabilities - can review, or restrict user-generated content.
     */
    MODERATOR,
}