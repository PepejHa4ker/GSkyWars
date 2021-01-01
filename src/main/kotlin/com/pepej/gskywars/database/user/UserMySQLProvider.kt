package com.pepej.gskywars.database.user

import com.pepej.gskywars.database.kit.KitRowMapper
import com.pepej.gskywars.model.Kit
import com.pepej.gskywars.model.User
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface UserMySQLProvider : UserDao  {
    @SqlQuery("SELECT EXISTS(SELECT 1 FROM sw_users WHERE id = :id)")
    override fun userExists(@Bind("id") id: String): Boolean

    @SqlQuery("SELECT * FROM sw_users")
    @RegisterRowMapper(UserRowMapper::class)
    override fun getAllUsers(): List<User?>

    @SqlQuery("SELECT id FROM sw_users")
    override fun getAllUserIds(): List<String?>

    @SqlQuery("SELECT * FROM sw_kits WHERE id = :id")
    @RegisterRowMapper(KitRowMapper::class)
    override fun getKits(@Bind("id") id: String): MutableList<Kit?>

    @SqlUpdate("INSERT IGNORE INTO sw_kits(id, kit_id) VALUES (:id, :kit_id)")
    override fun updateKits(@Bind("id") id: String, @Bind("kit_id") kitId: Int)

    @SqlQuery("SELECT * FROM sw_users WHERE id = :id")
    @RegisterRowMapper(UserRowMapper::class)
    override fun getUser(@Bind("id") id: String): User?

    @SqlUpdate("INSERT IGNORE INTO sw_users(id, username) VALUES (:id, :username)")
    override fun createUser(@Bind("id") id: String, @Bind("username") username: String)

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

    @SqlUpdate("DELETE FROM sw_users WHERE id = :id")
    override fun deleteUser(@Bind("id") id: String)
}