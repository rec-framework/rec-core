package net.kimleo.rec.model

import net.kimleo.rec.concept.Queryable

data class Entity(val name: String, val key: String, val relations: List<Relation>) {}

data class Ref<T>(val collection: Queryable<T>)

interface Relation {}

data class OneToMany(val left: Entity, val right: Entity)

data class ManyToOne(val left: Entity, val right: Entity)
