package com.ilimi.graph.cache.mgr.impl;

import static com.ilimi.graph.cache.factory.JedisFactory.getRedisConncetion;
import static com.ilimi.graph.cache.factory.JedisFactory.returnConnection;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ilimi.common.exception.ClientException;
import com.ilimi.common.exception.ServerException;
import com.ilimi.graph.cache.exception.GraphCacheErrorCodes;
import com.ilimi.graph.cache.util.CacheKeyGenerator;
import com.ilimi.graph.common.mgr.BaseGraphManager;
import redis.clients.jedis.Jedis;

public class SequenceCacheManager {

    private static BaseGraphManager manager;

    public static void createSequence(String graphId, String sequenceId, List<String> members) {
        if (!manager.validateRequired(sequenceId)) {
            throw new ClientException(GraphCacheErrorCodes.ERR_CACHE_CREATE_SEQ_ERROR.name(), "Required parameters are missing");
        }
        Jedis jedis = getRedisConncetion();
        try {
            String key = CacheKeyGenerator.getSequenceMembersKey(graphId, sequenceId);
            Map<String, Double> sortedMap = new HashMap<String, Double>();
            double i = 1;
            for (String memberId : members) {
                sortedMap.put(memberId, i);
                i += 1;
            }
            jedis.zadd(key, sortedMap);
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_CREATE_SEQ_ERROR.name(), e.getMessage());
        } finally {
            returnConnection(jedis);
        }
    }

    public static Long addSequenceMember(String graphId, String sequenceId, Long index, String memberId) {
        if (!manager.validateRequired(sequenceId, memberId)) {
            throw new ClientException(GraphCacheErrorCodes.ERR_CACHE_SEQ_ADD_MEMBER_ERROR.name(), "Required parameters are missing");
        }
        Jedis jedis = getRedisConncetion();
        try {
            String key = CacheKeyGenerator.getSequenceMembersKey(graphId, sequenceId);
            if (null == index || index.longValue() <= 0) {
                index = jedis.zcard(key) + 1;
            }
            jedis.zadd(key, index, memberId);
            return index;
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SEQ_ADD_MEMBER_ERROR.name(), e.getMessage());
        } finally {
            returnConnection(jedis);
        }
    }

    public static void removeSequenceMember(String graphId, String sequenceId, String memberId) {
        if (!manager.validateRequired(sequenceId, memberId)) {
            throw new ClientException(GraphCacheErrorCodes.ERR_CACHE_SEQ_REMOVE_MEMBER_ERROR.name(), "Required parameters are missing");
        }
        Jedis jedis = getRedisConncetion();
        try {
            String key = CacheKeyGenerator.getSequenceMembersKey(graphId, sequenceId);
            jedis.zrem(key, memberId);
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SEQ_REMOVE_MEMBER_ERROR.name(), e.getMessage());
        } finally {
            returnConnection(jedis);
        }
    }

    public static void dropSequence(String graphId, String sequenceId) {
        if (!manager.validateRequired(sequenceId)) {
            throw new ClientException(GraphCacheErrorCodes.ERR_CACHE_DROP_SEQ_ERROR.name(), "Required parameters are missing");
        }
        Jedis jedis = getRedisConncetion();
        try {
            String key = CacheKeyGenerator.getSequenceMembersKey(graphId, sequenceId);
            jedis.del(key);
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_DROP_SEQ_ERROR.name(), e.getMessage());
        } finally {
            returnConnection(jedis);
        }
    }

    public static List<String> getSequenceMembers(String graphId, String sequenceId) {
        if (!manager.validateRequired(sequenceId)) {
            throw new ClientException(GraphCacheErrorCodes.ERR_CACHE_SEQ_GET_MEMBERS_ERROR.name(), "Required parameters are missing");
        }
        Jedis jedis = getRedisConncetion();
        try {
            String key = CacheKeyGenerator.getSequenceMembersKey(graphId, sequenceId);
            Set<String> members = jedis.zrange(key, 0, -1);
            List<String> memberIds = new LinkedList<String>();
            if (null != members && !members.isEmpty()) {
                for (String memberId : members) {
                    memberIds.add(memberId);
                }
            }
            return memberIds;
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SEQ_GET_MEMBERS_ERROR.name(), e.getMessage(), e);
        } finally {
            returnConnection(jedis);
        }
    }

    public static Long getSequenceCardinality(String graphId, String sequenceId) {
        if (!manager.validateRequired(sequenceId)) {
            throw new ClientException(GraphCacheErrorCodes.ERR_CACHE_SEQ_GET_MEMBERS_ERROR.name(), "Required parameters are missing");
        }
        Jedis jedis = getRedisConncetion();
        try {
            String key = CacheKeyGenerator.getSequenceMembersKey(graphId, sequenceId);
            Long cardinality = jedis.zcard(key);
            return cardinality;
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SEQ_GET_MEMBERS_ERROR.name(), e.getMessage(), e);
        } finally {
            returnConnection(jedis);
        }
    }

    public static Boolean isSequenceMember(String graphId, String sequenceId, String memberId) {
        if (!manager.validateRequired(sequenceId, memberId)) {
            throw new ClientException(GraphCacheErrorCodes.ERR_CACHE_SEQ_GET_MEMBERS_ERROR.name(),
                    "IsSequenceMember: Required parameters are missing");
        }
        Jedis jedis = getRedisConncetion();
        try {
            String key = CacheKeyGenerator.getSequenceMembersKey(graphId, sequenceId);
            Double score = jedis.zscore(key, memberId);
            if (null == score || score.doubleValue() <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SEQ_GET_MEMBERS_ERROR.name(), e.getMessage(), e);
        } finally {
            returnConnection(jedis);
        }
    }

}
