package com.pepej.gskywars.database.kit

import com.pepej.gskywars.model.Kit
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class KitRowMapper : RowMapper<Kit> {

    override fun map(rs: ResultSet, ctx: StatementContext): Kit? {
        val id = rs.getInt("kit_id")
        return Kit.kits.find { it.id == id }

    }
}