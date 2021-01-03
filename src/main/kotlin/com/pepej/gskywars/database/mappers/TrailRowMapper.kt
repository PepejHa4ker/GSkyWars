package com.pepej.gskywars.database.mappers

import com.pepej.gskywars.model.Trail
import org.bukkit.Particle
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class TrailRowMapper : RowMapper<Trail> {
    override fun map(rs: ResultSet, ctx: StatementContext): Trail {
        return Trail(
            rs.getInt("trail_id"),
            rs.getString("trail_name"),
            rs.getString("trail_description"),
            Particle.valueOf(rs.getString("trail_particle")))
    }
}