/*
 *  Copyright 2017 Sebastian Raubach from the Information
 *  and Computational Sciences Group at JHI Dundee
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinatebuilder.server.uploader.s3;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

import java.io.*;
import java.text.*;

import jhi.germinatebuilder.server.util.*;

public class UploadObjectSingleOperation
{
	private static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");

	public static void upload(Logger logger, File file) throws IOException
	{
		String accessKey = PropertyReader.getProperty(PropertyReader.AMAZON_S3_ACCESS_KEY);
		String accessKeySecret = PropertyReader.getProperty(PropertyReader.AMAZON_S3_ACCESS_KEY_SECRET);
		String bucketName = PropertyReader.getProperty(PropertyReader.AMAZON_S3_BUCKET_NAME);
		String targetFile = PropertyReader.getProperty(PropertyReader.AMAZON_S3_TARGET_FOLDER);
		/* DATE/DATETIME-name: 2016-01-01/2016-01-01-12-12-12-maize.war */
		targetFile = String.format(targetFile, (SDF_DATE.format(file.lastModified()) + "/" + SDF_TIME.format(file.lastModified()) + "-" + file.getName()));

		/*  Create the client using the access key and secret*/
		AmazonS3Client s3client = new AmazonS3Client(new BasicAWSCredentials(accessKey, accessKeySecret));
		try
		{
			logger.log("Uploading file: " + file.getAbsolutePath() + " to " + targetFile);
			/* Upload the file to the target location in the bucket */
			s3client.putObject(new PutObjectRequest(bucketName, targetFile, file));
		}
		catch (AmazonClientException e)
		{
			e.printStackTrace();
			throw new IOException(e);
		}
	}
}
