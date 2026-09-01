-- Lets a password change invalidate every JWT issued before it, instead of
-- leaving a stolen/leaked token valid for up to its full 30-day lifetime
-- even after the account owner "secures" their account.
ALTER TABLE app_users ADD COLUMN token_version bigint NOT NULL DEFAULT 0;
