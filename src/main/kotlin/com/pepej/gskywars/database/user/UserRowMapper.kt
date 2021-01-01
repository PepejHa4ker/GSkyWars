package com.pepej.gskywars.database.user

import com.pepej.gskywars.model.User
import com.pepej.papi.utils.UndashedUuids
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet


class UserRowMapper : RowMapper<User> {

    override fun map(rs: ResultSet, ctx: StatementContext): User {
        val id = UndashedUuids.fromString(rs.getString("id"))
        val username = rs.getString("username")
        val reputation = rs.getInt("reputation")
        val lastVoteTimeStamp = rs.getLong("last_vote_timestamp")
        val activeKit = rs.getInt("active_kit")
        val games = rs.getInt("games")
        val wins = rs.getInt("wins")
        val kills = rs.getInt("kills")
        val deaths = rs.getInt("deaths")
        val arrowsFired = rs.getInt("arrows_fired")
        val blocksPlaced = rs.getInt("blocks_placed")
        val blockBroken = rs.getInt("blocks_broken")

        return User(
            id = id,
            username = username,
            activeKit = activeKit,
            reputation = reputation,
            lastVoteTimeStamp = lastVoteTimeStamp,
            games = games,
            wins = wins,
            kills = kills,
            deaths = deaths,
            arrowsFired = arrowsFired,
            blocksPlaced = blocksPlaced,
            blocksBroken = blockBroken
        )
    }
}