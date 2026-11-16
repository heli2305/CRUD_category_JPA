IF DB_ID('ShoppingServiceMVC') IS NULL
BEGIN
    CREATE DATABASE ShoppingServiceMVC;
END
GO

USE ShoppingServiceMVC;
GO

IF OBJECT_ID('dbo.[User]', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.[User] (
        id INT IDENTITY(1,1) PRIMARY KEY,
        email NVARCHAR(255) NULL,
        username NVARCHAR(255) NOT NULL UNIQUE,
        fullname NVARCHAR(255) NULL,
        password NVARCHAR(255) NOT NULL,
        avatar NVARCHAR(255) NULL,
        roleid INT NOT NULL DEFAULT 5,
        phone NVARCHAR(255) NULL,
        createddate DATE NULL,
        status INT NOT NULL DEFAULT 1,
        otp NVARCHAR(10) NULL,
        otp_expiry DATETIME2 NULL
    );
END
GO

IF COL_LENGTH('dbo.[User]', 'status') IS NULL
BEGIN
    ALTER TABLE dbo.[User] ADD status INT NOT NULL DEFAULT 1;
END
GO

IF COL_LENGTH('dbo.[User]', 'otp') IS NULL
BEGIN
    ALTER TABLE dbo.[User] ADD otp NVARCHAR(10) NULL;
END
GO

IF COL_LENGTH('dbo.[User]', 'otp_expiry') IS NULL
BEGIN
    ALTER TABLE dbo.[User] ADD otp_expiry DATETIME2 NULL;
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.[User] WHERE username = 'admin')
BEGIN
    INSERT INTO dbo.[User] (email, username, fullname, password, avatar, roleid, phone, createddate)
    VALUES ('admin@gmail.com', 'admin', N'Admin', '$2a$10$PXHjIrtBimozNAd25WYr1OZzyyegP6wsGVhzhV2mTZbjkMqc1GRPa', 'avata.png', 1, '0987654321', GETDATE());
END
ELSE
BEGIN
    UPDATE dbo.[User]
    SET password = '$2a$10$PXHjIrtBimozNAd25WYr1OZzyyegP6wsGVhzhV2mTZbjkMqc1GRPa'
    WHERE username = 'admin' AND password = '123';
END
GO

IF OBJECT_ID('dbo.categories', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.categories (
        CategoryId INT IDENTITY(1,1) PRIMARY KEY,
        CategoryName NVARCHAR(50) NOT NULL,
        Images NVARCHAR(500) NULL,
        status INT NOT NULL DEFAULT 1
    );
END
GO

IF OBJECT_ID('dbo.Videos', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Videos (
        VideoId NVARCHAR(255) PRIMARY KEY,
        Active INT NOT NULL DEFAULT 1,
        Description NVARCHAR(500) NULL,
        Poster NVARCHAR(500) NULL,
        Title NVARCHAR(500) NULL,
        Views INT NOT NULL DEFAULT 0,
        CategoryId INT NULL,
        CONSTRAINT FK_Videos_Categories FOREIGN KEY (CategoryId)
            REFERENCES dbo.categories(CategoryId)
    );
END
GO
