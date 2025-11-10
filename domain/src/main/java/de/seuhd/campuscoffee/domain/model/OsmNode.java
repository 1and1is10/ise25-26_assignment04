package de.seuhd.campuscoffee.domain.model;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * Represents an OpenStreetMap node with relevant Point of Sale information.
 * Includes all parsed OSM tags as a map for flexible access.
 */
@Builder
public record OsmNode(
        @NonNull Long nodeId,
        @NonNull Map<String, String> tags
) {}
