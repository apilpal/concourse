/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Jeff Nelson, Cinchapi Software Collective
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cinchapi.concourse.server.io;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import org.cinchapi.concourse.annotate.DoNotInvoke;
import org.cinchapi.concourse.util.ByteBuffers;

/**
 * An Byteable object that wraps multiple other Byteable objects into a single
 * element.
 * 
 * @author jnelson
 */
@Immutable
public final class ByteableComposite implements Byteable {

	/**
	 * Return a ByteableComposite for the list of {@code byteables}.
	 * 
	 * @param byteables
	 * @return the ByteableComposite
	 */
	public static ByteableComposite create(Byteable... byteables) {
		return new ByteableComposite(byteables);
	}

	/**
	 * Return the ByteableComposite encoded in {@code bytes} so long as those
	 * bytes adhere to the format specified by the {@link #getBytes()} method.
	 * This method assumes that all the bytes in the {@code bytes} belong to the
	 * ByteableComposite. In general, it is necessary to get the appropriate
	 * Value slice from the parent ByteBuffer using
	 * {@link ByteBuffers#slice(ByteBuffer, int, int)}.
	 * 
	 * @param bytes
	 * @return the ByteableComposite
	 */
	public static ByteableComposite fromByteBuffer(ByteBuffer bytes) {
		return Byteables.read(bytes, ByteableComposite.class);
	}

	private final ByteBuffer bytes;

	/**
	 * Construct an instance that represents an existing ByteableComposite from
	 * a ByteBuffer. This constructor is public so as to comply with the
	 * {@link Byteable} interface. Calling this constructor directly is not
	 * recommend. Use {@link #fromByteBuffer(ByteBuffer)} instead to take
	 * advantage of reference caching.
	 * 
	 * @param bytes
	 */
	@DoNotInvoke
	public ByteableComposite(ByteBuffer bytes) {
		this.bytes = bytes;
	}

	/**
	 * Construct a new instance.
	 * 
	 * @param byteables
	 */
	private ByteableComposite(Byteable... byteables) {
		int size = 0;
		for (Byteable byteable : byteables) {
			size += byteable.size();
		}
		bytes = ByteBuffer.allocate(size);
		for (Byteable byteable : byteables) {
			bytes.put(byteable.getBytes());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ByteableComposite){
			ByteableComposite other = (ByteableComposite) obj;
			return getBytes().equals(other.getBytes());
		}
		return false;
	}

	@Override
	public ByteBuffer getBytes() {
		return ByteBuffers.asReadOnlyBuffer(bytes);
	}

	@Override
	public int hashCode() {
		return getBytes().hashCode();
	}

	@Override
	public int size() {
		return bytes.capacity();
	}
	
	

}