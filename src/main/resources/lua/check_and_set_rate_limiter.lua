local key = KEYS[1]
local jsonString = redis.call('GET', key)
local now = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local requestedTokens = tonumber(ARGV[3])
local bucketCapacity = tonumber(ARGV[4])
local defaultBucketSize = tonumber(ARGV[5])
local tokenBucket

-- Log all KEYS
for i, inputKey in ipairs(KEYS) do
    redis.log(redis.LOG_NOTICE, "KEY[" .. i .. "]: " .. inputKey)
end

-- Log all ARGV
for i, arg in ipairs(ARGV) do
    redis.log(redis.LOG_NOTICE, "ARGV[" .. i .. "]: " .. arg)
end

-- Initialize token bucket
if jsonString then
    tokenBucket = cjson.decode(jsonString)
else
    tokenBucket = cjson.decode(cjson.encode({
        ['tokens'] = defaultBucketSize,
        ['lastRefillTimestamp'] = now
    }))
end

redis.log(redis.LOG_DEBUG, "Token Bucket Before " .. cjson.encode(tokenBucket))

-- Calculate how many tokens to fill
local tokensToFill = math.floor((now - tokenBucket.lastRefillTimestamp) / refillRate)

redis.log(redis.LOG_DEBUG, "Tokens to fill " .. tokensToFill)
-- If tokens can be refilled, update the bucket
if tokensToFill > 0 then
    tokenBucket.lastRefillTimestamp = tokenBucket.lastRefillTimestamp + tokensToFill * refillRate
    tokenBucket.tokens = tokenBucket.tokens + tokensToFill
end

-- Ensure the token count doesn't exceed the bucket capacity
if tokenBucket.tokens > bucketCapacity then
    tokenBucket.tokens = bucketCapacity
end

-- Check if there are enough tokens to satisfy the request
if tokenBucket.tokens < requestedTokens then
    -- Not enough tokens, update Redis and return false
    redis.call('SET', key, cjson.encode(tokenBucket))
    return false
end

-- Deduct the requested tokens from the bucket
tokenBucket.tokens = tokenBucket.tokens - requestedTokens

-- Save the updated token bucket back to Redis
redis.call('SET', key, cjson.encode(tokenBucket))

redis.log(redis.LOG_DEBUG, "Token Bucket after " .. cjson.encode(tokenBucket))
-- Return true to indicate success
return true