package com.pepej.gskywars.database.user

import com.pepej.gskywars.database.mappers.KitRowMapper
import com.pepej.gskywars.database.mappers.TrailRowMapper
import com.pepej.gskywars.database.mappers.UserRowMapper
import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.Trail
import com.pepej.gskywars.model.User
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
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

    @SqlUpdate("INSERT IGNORE INTO sw_users(id, username) VALUES (?, ?)")
    override fun createUser(id: String, username: String)

    @SqlUpdate("UPDATE sw_users SET reputation = :user.reputation, last_vote_timestamp = :user.lastVoteTimeStamp, active_kit = :user.activeKit, active_trail = :user.activeTrail, games = :user.games, wins = :user.wins, kills = :user.kills, deaths = :user.deaths, arrows_fired = :user.arrowsFired, blocks_placed = :user.blocksPlaced, blocks_broken = :user.blocksBroken WHERE id = :user.id")
    override fun updateUser(user: User)

    @SqlUpdate("DELETE FROM sw_users WHERE id = ?")
    override fun deleteUser(id: String)
}