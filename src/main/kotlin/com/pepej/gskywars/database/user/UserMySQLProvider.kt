package com.pepej.gskywars.database.user

import com.pepej.gskywars.database.mappers.KitRowMapper
import com.pepej.gskywars.database.mappers.TrailRowMapper
import com.pepej.gskywars.database.mappers.UserRowMapper
import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.Trail
import com.pepej.gskywars.model.User
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface UserMySQLProvider : UserDao  {
    @SqlQuery("SELECT EXISTS(SELECT 1 FROM sw_users WHERE id = ?)")
    override fun userExists(id: String): Boolean

    @SqlQuery("SELECT * FROM sw_users")
    @RegisterRowMapper(UserRowMapper::class)
    override fun getAllUsers(): List<User?>

    @SqlQuery("SELECT id FROM sw_users")
    override fun getAllUserIds(): List<String?>

    @SqlQuery("SELECT * FROM sw_kits WHERE id = ?")
    @RegisterRowMapper(KitRowMapper::class)
    override fun getKits(id: String): MutableList<Kit?>

    @SqlQuery("SELECT * FROM sw_trails WHERE id = ?")
    @RegisterRowMapper(TrailRowMapper::class)
    override fun getTrails(id: String): MutableList<Trail?>

    @SqlUpdate("INSERT IGNORE INTO sw_kits(id, kit_id) VALUES (?, ?)")
    override fun updateKit(id: String, kitId: Int)

    @SqlUpdate("INSERT IGNORE INTO sw_trails(id, trail_id, trail_name, trail_description, trail_particle) VALUES (:id, :trail.id, :trail.name, :trail.description, :trail.particle)")
    override fun updateTrail(id: String, trail: Trail)

    @SqlQuery("SELECT * FROM sw_users WHERE id = ?")
    @RegisterRowMapper(UserRowMapper::class)
    override fun getUser( id: String): User?

    @SqlQuery("SELECT * FROM sw_users WHERE username = ?")
    @RegisterRowMapper(UserRowMapper::class)
    override fun getUserByName( name: String): User?

    @SqlUpdate("INSERT IGNORE INTO sw_users(id, username) VALUES (?, ?)")
    override fun createUser(id: String, username: String)

    @SqlUpdate("UPDATE sw_users SET reputation = :reputation, last_vote_timestamp = :lastVoteTimeStamp, active_kit = :activeKit, games = :games, wins = :wins, kills = :kills, deaths = :deaths, arrows_fired = :arrowsFired, blocks_placed = :blocksPlaced, blocks_broken = :blocksBroken WHERE id = :id")
    override fun updateUser(
        @Bind("id") id: String,
        @Bind("activeKit") activeKit: Int,
        @Bind("reputation") reputation: Int,
        @Bind("lastVoteTimeStamp") lastVoteTimeStamp: Long,
        @Bind("games") games: Int,
        @Bind("wins") wins: Int,
        @Bind("kills") kills: Int,
        @Bind("deaths") deaths: Int,
        @Bind("arrowsFired") arrowsFired: Int,
        @Bind("blocksPlaced") blocksPlaced: Int,
        @Bind("blocksBroken") blocksBroken: Int
    )

    @SqlUpdate("DELETE FROM sw_users WHERE id = ?")
    override fun deleteUser(id: String)
}