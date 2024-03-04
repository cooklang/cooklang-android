// This file was autogenerated by some hot garbage in the `uniffi` crate.
// Trust me, you don't want to mess with it!

@file:Suppress("NAME_SHADOWING")

package uniffi.cooklang_sync_client

// Common helper code.
//
// Ideally this would live in a separate .kt file where it can be unittested etc
// in isolation, and perhaps even published as a re-useable package.
//
// However, it's important that the details of how this helper code works (e.g. the
// way that different builtin types are passed across the FFI) exactly match what's
// expected by the Rust code on the other side of the interface. In practice right
// now that means coming from the exact some version of `uniffi` that was used to
// compile the Rust component. The easiest way to ensure this is to bundle the Kotlin
// helpers directly inline like we're doing here.

import com.sun.jna.Callback
import com.sun.jna.IntegerType
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.ptr.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.CharBuffer
import java.nio.charset.CodingErrorAction
import java.util.concurrent.ConcurrentHashMap

// This is a helper for safely working with byte buffers returned from the Rust code.
// A rust-owned buffer is represented by its capacity, its current length, and a
// pointer to the underlying data.

@Structure.FieldOrder("capacity", "len", "data")
open class RustBuffer : Structure() {
    @JvmField var capacity: Int = 0

    @JvmField var len: Int = 0

    @JvmField var data: Pointer? = null

    class ByValue : RustBuffer(), Structure.ByValue
    class ByReference : RustBuffer(), Structure.ByReference

    companion object {
        internal fun alloc(size: Int = 0) = uniffiRustCall() { status ->
            UniffiLib.INSTANCE.ffi_cooklang_sync_client_rustbuffer_alloc(size, status)
        }.also {
            if (it.data == null) {
                throw RuntimeException("RustBuffer.alloc() returned null data pointer (size=$size)")
            }
        }

        internal fun create(capacity: Int, len: Int, data: Pointer?): RustBuffer.ByValue {
            var buf = RustBuffer.ByValue()
            buf.capacity = capacity
            buf.len = len
            buf.data = data
            return buf
        }

        internal fun free(buf: RustBuffer.ByValue) = uniffiRustCall() { status ->
            UniffiLib.INSTANCE.ffi_cooklang_sync_client_rustbuffer_free(buf, status)
        }
    }

    @Suppress("TooGenericExceptionThrown")
    fun asByteBuffer() =
        this.data?.getByteBuffer(0, this.len.toLong())?.also {
            it.order(ByteOrder.BIG_ENDIAN)
        }
}

/**
 * The equivalent of the `*mut RustBuffer` type.
 * Required for callbacks taking in an out pointer.
 *
 * Size is the sum of all values in the struct.
 */
class RustBufferByReference : ByReference(16) {
    /**
     * Set the pointed-to `RustBuffer` to the given value.
     */
    fun setValue(value: RustBuffer.ByValue) {
        // NOTE: The offsets are as they are in the C-like struct.
        val pointer = getPointer()
        pointer.setInt(0, value.capacity)
        pointer.setInt(4, value.len)
        pointer.setPointer(8, value.data)
    }

    /**
     * Get a `RustBuffer.ByValue` from this reference.
     */
    fun getValue(): RustBuffer.ByValue {
        val pointer = getPointer()
        val value = RustBuffer.ByValue()
        value.writeField("capacity", pointer.getInt(0))
        value.writeField("len", pointer.getInt(4))
        value.writeField("data", pointer.getPointer(8))

        return value
    }
}

// This is a helper for safely passing byte references into the rust code.
// It's not actually used at the moment, because there aren't many things that you
// can take a direct pointer to in the JVM, and if we're going to copy something
// then we might as well copy it into a `RustBuffer`. But it's here for API
// completeness.

@Structure.FieldOrder("len", "data")
open class ForeignBytes : Structure() {
    @JvmField var len: Int = 0

    @JvmField var data: Pointer? = null

    class ByValue : ForeignBytes(), Structure.ByValue
}

// The FfiConverter interface handles converter types to and from the FFI
//
// All implementing objects should be public to support external types.  When a
// type is external we need to import it's FfiConverter.
public interface FfiConverter<KotlinType, FfiType> {
    // Convert an FFI type to a Kotlin type
    fun lift(value: FfiType): KotlinType

    // Convert an Kotlin type to an FFI type
    fun lower(value: KotlinType): FfiType

    // Read a Kotlin type from a `ByteBuffer`
    fun read(buf: ByteBuffer): KotlinType

    // Calculate bytes to allocate when creating a `RustBuffer`
    //
    // This must return at least as many bytes as the write() function will
    // write. It can return more bytes than needed, for example when writing
    // Strings we can't know the exact bytes needed until we the UTF-8
    // encoding, so we pessimistically allocate the largest size possible (3
    // bytes per codepoint).  Allocating extra bytes is not really a big deal
    // because the `RustBuffer` is short-lived.
    fun allocationSize(value: KotlinType): Int

    // Write a Kotlin type to a `ByteBuffer`
    fun write(value: KotlinType, buf: ByteBuffer)

    // Lower a value into a `RustBuffer`
    //
    // This method lowers a value into a `RustBuffer` rather than the normal
    // FfiType.  It's used by the callback interface code.  Callback interface
    // returns are always serialized into a `RustBuffer` regardless of their
    // normal FFI type.
    fun lowerIntoRustBuffer(value: KotlinType): RustBuffer.ByValue {
        val rbuf = RustBuffer.alloc(allocationSize(value))
        try {
            val bbuf = rbuf.data!!.getByteBuffer(0, rbuf.capacity.toLong()).also {
                it.order(ByteOrder.BIG_ENDIAN)
            }
            write(value, bbuf)
            rbuf.writeField("len", bbuf.position())
            return rbuf
        } catch (e: Throwable) {
            RustBuffer.free(rbuf)
            throw e
        }
    }

    // Lift a value from a `RustBuffer`.
    //
    // This here mostly because of the symmetry with `lowerIntoRustBuffer()`.
    // It's currently only used by the `FfiConverterRustBuffer` class below.
    fun liftFromRustBuffer(rbuf: RustBuffer.ByValue): KotlinType {
        val byteBuf = rbuf.asByteBuffer()!!
        try {
            val item = read(byteBuf)
            if (byteBuf.hasRemaining()) {
                throw RuntimeException("junk remaining in buffer after lifting, something is very wrong!!")
            }
            return item
        } finally {
            RustBuffer.free(rbuf)
        }
    }
}

// FfiConverter that uses `RustBuffer` as the FfiType
public interface FfiConverterRustBuffer<KotlinType> : FfiConverter<KotlinType, RustBuffer.ByValue> {
    override fun lift(value: RustBuffer.ByValue) = liftFromRustBuffer(value)
    override fun lower(value: KotlinType) = lowerIntoRustBuffer(value)
}

// A handful of classes and functions to support the generated data structures.
// This would be a good candidate for isolating in its own ffi-support lib.
// Error runtime.
@Structure.FieldOrder("code", "error_buf")
internal open class UniffiRustCallStatus : Structure() {
    @JvmField var code: Byte = 0

    @JvmField var error_buf: RustBuffer.ByValue = RustBuffer.ByValue()

    class ByValue : UniffiRustCallStatus(), Structure.ByValue

    fun isSuccess(): Boolean {
        return code == 0.toByte()
    }

    fun isError(): Boolean {
        return code == 1.toByte()
    }

    fun isPanic(): Boolean {
        return code == 2.toByte()
    }
}

class InternalException(message: String) : Exception(message)

// Each top-level error class has a companion object that can lift the error from the call status's rust buffer
interface UniffiRustCallStatusErrorHandler<E> {
    fun lift(error_buf: RustBuffer.ByValue): E
}

// Helpers for calling Rust
// In practice we usually need to be synchronized to call this safely, so it doesn't
// synchronize itself

// Call a rust function that returns a Result<>.  Pass in the Error class companion that corresponds to the Err
private inline fun <U, E : Exception> uniffiRustCallWithError(errorHandler: UniffiRustCallStatusErrorHandler<E>, callback: (UniffiRustCallStatus) -> U): U {
    var status = UniffiRustCallStatus()
    val return_value = callback(status)
    uniffiCheckCallStatus(errorHandler, status)
    return return_value
}

// Check UniffiRustCallStatus and throw an error if the call wasn't successful
private fun<E : Exception> uniffiCheckCallStatus(errorHandler: UniffiRustCallStatusErrorHandler<E>, status: UniffiRustCallStatus) {
    if (status.isSuccess()) {
        return
    } else if (status.isError()) {
        throw errorHandler.lift(status.error_buf)
    } else if (status.isPanic()) {
        // when the rust code sees a panic, it tries to construct a rustbuffer
        // with the message.  but if that code panics, then it just sends back
        // an empty buffer.
        if (status.error_buf.len > 0) {
            throw InternalException(FfiConverterString.lift(status.error_buf))
        } else {
            throw InternalException("Rust panic")
        }
    } else {
        throw InternalException("Unknown rust call status: $status.code")
    }
}

// UniffiRustCallStatusErrorHandler implementation for times when we don't expect a CALL_ERROR
object UniffiNullRustCallStatusErrorHandler : UniffiRustCallStatusErrorHandler<InternalException> {
    override fun lift(error_buf: RustBuffer.ByValue): InternalException {
        RustBuffer.free(error_buf)
        return InternalException("Unexpected CALL_ERROR")
    }
}

// Call a rust function that returns a plain value
private inline fun <U> uniffiRustCall(callback: (UniffiRustCallStatus) -> U): U {
    return uniffiRustCallWithError(UniffiNullRustCallStatusErrorHandler, callback)
}

// IntegerType that matches Rust's `usize` / C's `size_t`
public class USize(value: Long = 0) : IntegerType(Native.SIZE_T_SIZE, value, true) {
    // This is needed to fill in the gaps of IntegerType's implementation of Number for Kotlin.
    override fun toByte() = toInt().toByte()

    // Needed until https://youtrack.jetbrains.com/issue/KT-47902 is fixed.
    @Deprecated("`toInt().toChar()` is deprecated")
    override fun toChar() = toInt().toChar()
    override fun toShort() = toInt().toShort()

    fun writeToBuffer(buf: ByteBuffer) {
        // Make sure we always write usize integers using native byte-order, since they may be
        // casted to pointer values
        buf.order(ByteOrder.nativeOrder())
        try {
            when (Native.SIZE_T_SIZE) {
                4 -> buf.putInt(toInt())
                8 -> buf.putLong(toLong())
                else -> throw RuntimeException("Invalid SIZE_T_SIZE: ${Native.SIZE_T_SIZE}")
            }
        } finally {
            buf.order(ByteOrder.BIG_ENDIAN)
        }
    }

    companion object {
        val size: Int
            get() = Native.SIZE_T_SIZE

        fun readFromBuffer(buf: ByteBuffer): USize {
            // Make sure we always read usize integers using native byte-order, since they may be
            // casted from pointer values
            buf.order(ByteOrder.nativeOrder())
            try {
                return when (Native.SIZE_T_SIZE) {
                    4 -> USize(buf.getInt().toLong())
                    8 -> USize(buf.getLong())
                    else -> throw RuntimeException("Invalid SIZE_T_SIZE: ${Native.SIZE_T_SIZE}")
                }
            } finally {
                buf.order(ByteOrder.BIG_ENDIAN)
            }
        }
    }
}

// Map handles to objects
//
// This is used when the Rust code expects an opaque pointer to represent some foreign object.
// Normally we would pass a pointer to the object, but JNA doesn't support getting a pointer from an
// object reference , nor does it support leaking a reference to Rust.
//
// Instead, this class maps USize values to objects so that we can pass a pointer-sized type to
// Rust when it needs an opaque pointer.
//
// TODO: refactor callbacks to use this class
internal class UniFfiHandleMap<T : Any> {
    private val map = ConcurrentHashMap<USize, T>()

    // Use AtomicInteger for our counter, since we may be on a 32-bit system.  4 billion possible
    // values seems like enough. If somehow we generate 4 billion handles, then this will wrap
    // around back to zero and we can assume the first handle generated will have been dropped by
    // then.
    private val counter = java.util.concurrent.atomic.AtomicInteger(0)

    val size: Int
        get() = map.size

    fun insert(obj: T): USize {
        val handle = USize(counter.getAndAdd(1).toLong())
        map.put(handle, obj)
        return handle
    }

    fun get(handle: USize): T? {
        return map.get(handle)
    }

    fun remove(handle: USize): T? {
        return map.remove(handle)
    }
}

// FFI type for Rust future continuations
internal interface UniFffiRustFutureContinuationCallbackType : com.sun.jna.Callback {
    fun callback(continuationHandle: USize, pollResult: Byte)
}

// Contains loading, initialization code,
// and the FFI Function declarations in a com.sun.jna.Library.
@Synchronized
private fun findLibraryName(componentName: String): String {
    val libOverride = System.getProperty("uniffi.component.$componentName.libraryOverride")
    if (libOverride != null) {
        return libOverride
    }
    return "cooklang_sync_client"
}

private inline fun <reified Lib : Library> loadIndirect(
    componentName: String,
): Lib {
    return Native.load<Lib>(findLibraryName(componentName), Lib::class.java)
}

// A JNA Library to expose the extern-C FFI definitions.
// This is an implementation detail which will be called internally by the public API.

internal interface UniffiLib : Library {
    companion object {
        internal val INSTANCE: UniffiLib by lazy {
            loadIndirect<UniffiLib>(componentName = "cooklang_sync_client")
                .also { lib: UniffiLib ->
                    uniffiCheckContractApiVersion(lib)
                    uniffiCheckApiChecksums(lib)
                }
        }
    }

    fun uniffi_cooklang_sync_client_fn_func_run(
        `storageDir`: RustBuffer.ByValue,
        `dbFilePath`: RustBuffer.ByValue,
        `apiEndpoint`: RustBuffer.ByValue,
        `remoteToken`: RustBuffer.ByValue,
        uniffi_out_err: UniffiRustCallStatus,
    ): Unit
    fun ffi_cooklang_sync_client_rustbuffer_alloc(
        `size`: Int,
        uniffi_out_err: UniffiRustCallStatus,
    ): RustBuffer.ByValue
    fun ffi_cooklang_sync_client_rustbuffer_from_bytes(
        `bytes`: ForeignBytes.ByValue,
        uniffi_out_err: UniffiRustCallStatus,
    ): RustBuffer.ByValue
    fun ffi_cooklang_sync_client_rustbuffer_free(
        `buf`: RustBuffer.ByValue,
        uniffi_out_err: UniffiRustCallStatus,
    ): Unit
    fun ffi_cooklang_sync_client_rustbuffer_reserve(
        `buf`: RustBuffer.ByValue,
        `additional`: Int,
        uniffi_out_err: UniffiRustCallStatus,
    ): RustBuffer.ByValue
    fun ffi_cooklang_sync_client_rust_future_poll_u8(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_u8(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_u8(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_u8(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Byte
    fun ffi_cooklang_sync_client_rust_future_poll_i8(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_i8(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_i8(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_i8(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Byte
    fun ffi_cooklang_sync_client_rust_future_poll_u16(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_u16(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_u16(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_u16(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Short
    fun ffi_cooklang_sync_client_rust_future_poll_i16(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_i16(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_i16(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_i16(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Short
    fun ffi_cooklang_sync_client_rust_future_poll_u32(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_u32(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_u32(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_u32(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Int
    fun ffi_cooklang_sync_client_rust_future_poll_i32(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_i32(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_i32(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_i32(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Int
    fun ffi_cooklang_sync_client_rust_future_poll_u64(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_u64(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_u64(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_u64(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Long
    fun ffi_cooklang_sync_client_rust_future_poll_i64(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_i64(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_i64(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_i64(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Long
    fun ffi_cooklang_sync_client_rust_future_poll_f32(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_f32(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_f32(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_f32(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Float
    fun ffi_cooklang_sync_client_rust_future_poll_f64(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_f64(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_f64(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_f64(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Double
    fun ffi_cooklang_sync_client_rust_future_poll_pointer(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_pointer(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_pointer(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_pointer(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Pointer
    fun ffi_cooklang_sync_client_rust_future_poll_rust_buffer(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_rust_buffer(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_rust_buffer(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_rust_buffer(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): RustBuffer.ByValue
    fun ffi_cooklang_sync_client_rust_future_poll_void(
        `handle`: Pointer,
        `callback`: UniFffiRustFutureContinuationCallbackType,
        `callbackData`: USize,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_cancel_void(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_free_void(
        `handle`: Pointer,
    ): Unit
    fun ffi_cooklang_sync_client_rust_future_complete_void(
        `handle`: Pointer,
        uniffi_out_err: UniffiRustCallStatus,
    ): Unit
    fun uniffi_cooklang_sync_client_checksum_func_run(): Short
    fun ffi_cooklang_sync_client_uniffi_contract_version(): Int
}

private fun uniffiCheckContractApiVersion(lib: UniffiLib) {
    // Get the bindings contract version from our ComponentInterface
    val bindings_contract_version = 25
    // Get the scaffolding contract version by calling the into the dylib
    val scaffolding_contract_version = lib.ffi_cooklang_sync_client_uniffi_contract_version()
    if (bindings_contract_version != scaffolding_contract_version) {
        throw RuntimeException("UniFFI contract version mismatch: try cleaning and rebuilding your project")
    }
}

@Suppress("UNUSED_PARAMETER")
private fun uniffiCheckApiChecksums(lib: UniffiLib) {
    if (lib.uniffi_cooklang_sync_client_checksum_func_run() != 22462.toShort()) {
        throw RuntimeException("UniFFI API checksum mismatch: try cleaning and rebuilding your project")
    }
}

// Async support

// Public interface members begin here.

// Interface implemented by anything that can contain an object reference.
//
// Such types expose a `destroy()` method that must be called to cleanly
// dispose of the contained objects. Failure to call this method may result
// in memory leaks.
//
// The easiest way to ensure this method is called is to use the `.use`
// helper method to execute a block and destroy the object at the end.
interface Disposable {
    fun destroy()
    companion object {
        fun destroy(vararg args: Any?) {
            args.filterIsInstance<Disposable>()
                .forEach(Disposable::destroy)
        }
    }
}

inline fun <T : Disposable?, R> T.use(block: (T) -> R) =
    try {
        block(this)
    } finally {
        try {
            // N.B. our implementation is on the nullable type `Disposable?`.
            this?.destroy()
        } catch (e: Throwable) {
            // swallow
        }
    }

public object FfiConverterString : FfiConverter<String, RustBuffer.ByValue> {
    // Note: we don't inherit from FfiConverterRustBuffer, because we use a
    // special encoding when lowering/lifting.  We can use `RustBuffer.len` to
    // store our length and avoid writing it out to the buffer.
    override fun lift(value: RustBuffer.ByValue): String {
        try {
            val byteArr = ByteArray(value.len)
            value.asByteBuffer()!!.get(byteArr)
            return byteArr.toString(Charsets.UTF_8)
        } finally {
            RustBuffer.free(value)
        }
    }

    override fun read(buf: ByteBuffer): String {
        val len = buf.getInt()
        val byteArr = ByteArray(len)
        buf.get(byteArr)
        return byteArr.toString(Charsets.UTF_8)
    }

    fun toUtf8(value: String): ByteBuffer {
        // Make sure we don't have invalid UTF-16, check for lone surrogates.
        return Charsets.UTF_8.newEncoder().run {
            onMalformedInput(CodingErrorAction.REPORT)
            encode(CharBuffer.wrap(value))
        }
    }

    override fun lower(value: String): RustBuffer.ByValue {
        val byteBuf = toUtf8(value)
        // Ideally we'd pass these bytes to `ffi_bytebuffer_from_bytes`, but doing so would require us
        // to copy them into a JNA `Memory`. So we might as well directly copy them into a `RustBuffer`.
        val rbuf = RustBuffer.alloc(byteBuf.limit())
        rbuf.asByteBuffer()!!.put(byteBuf)
        return rbuf
    }

    // We aren't sure exactly how many bytes our string will be once it's UTF-8
    // encoded.  Allocate 3 bytes per UTF-16 code unit which will always be
    // enough.
    override fun allocationSize(value: String): Int {
        val sizeForLength = 4
        val sizeForString = value.length * 3
        return sizeForLength + sizeForString
    }

    override fun write(value: String, buf: ByteBuffer) {
        val byteBuf = toUtf8(value)
        buf.putInt(byteBuf.limit())
        buf.put(byteBuf)
    }
}

sealed class SyncException(message: String) : Exception(message) {

    class IoException(message: String) : SyncException(message)

    class NotifyException(message: String) : SyncException(message)

    class StripPrefix(message: String) : SyncException(message)

    class SystemTime(message: String) : SyncException(message)

    class Convert(message: String) : SyncException(message)

    class DbQueryException(message: String) : SyncException(message)

    class ReqwestException(message: String) : SyncException(message)

    class ChannelSendException(message: String) : SyncException(message)

    class ConnectionInitException(message: String) : SyncException(message)

    class Unauthorized(message: String) : SyncException(message)

    class BodyExtractException(message: String) : SyncException(message)

    class GetFromCacheException(message: String) : SyncException(message)

    class Unknown(message: String) : SyncException(message)

    companion object ErrorHandler : UniffiRustCallStatusErrorHandler<SyncException> {
        override fun lift(error_buf: RustBuffer.ByValue): SyncException = FfiConverterTypeSyncError.lift(error_buf)
    }
}

public object FfiConverterTypeSyncError : FfiConverterRustBuffer<SyncException> {
    override fun read(buf: ByteBuffer): SyncException {
        return when (buf.getInt()) {
            1 -> SyncException.IoException(FfiConverterString.read(buf))
            2 -> SyncException.NotifyException(FfiConverterString.read(buf))
            3 -> SyncException.StripPrefix(FfiConverterString.read(buf))
            4 -> SyncException.SystemTime(FfiConverterString.read(buf))
            5 -> SyncException.Convert(FfiConverterString.read(buf))
            6 -> SyncException.DbQueryException(FfiConverterString.read(buf))
            7 -> SyncException.ReqwestException(FfiConverterString.read(buf))
            8 -> SyncException.ChannelSendException(FfiConverterString.read(buf))
            9 -> SyncException.ConnectionInitException(FfiConverterString.read(buf))
            10 -> SyncException.Unauthorized(FfiConverterString.read(buf))
            11 -> SyncException.BodyExtractException(FfiConverterString.read(buf))
            12 -> SyncException.GetFromCacheException(FfiConverterString.read(buf))
            13 -> SyncException.Unknown(FfiConverterString.read(buf))
            else -> throw RuntimeException("invalid error enum value, something is very wrong!!")
        }
    }

    override fun allocationSize(value: SyncException): Int {
        return 4
    }

    override fun write(value: SyncException, buf: ByteBuffer) {
        when (value) {
            is SyncException.IoException -> {
                buf.putInt(1)
                Unit
            }
            is SyncException.NotifyException -> {
                buf.putInt(2)
                Unit
            }
            is SyncException.StripPrefix -> {
                buf.putInt(3)
                Unit
            }
            is SyncException.SystemTime -> {
                buf.putInt(4)
                Unit
            }
            is SyncException.Convert -> {
                buf.putInt(5)
                Unit
            }
            is SyncException.DbQueryException -> {
                buf.putInt(6)
                Unit
            }
            is SyncException.ReqwestException -> {
                buf.putInt(7)
                Unit
            }
            is SyncException.ChannelSendException -> {
                buf.putInt(8)
                Unit
            }
            is SyncException.ConnectionInitException -> {
                buf.putInt(9)
                Unit
            }
            is SyncException.Unauthorized -> {
                buf.putInt(10)
                Unit
            }
            is SyncException.BodyExtractException -> {
                buf.putInt(11)
                Unit
            }
            is SyncException.GetFromCacheException -> {
                buf.putInt(12)
                Unit
            }
            is SyncException.Unknown -> {
                buf.putInt(13)
                Unit
            }
        }.let { /* this makes the `when` an expression, which ensures it is exhaustive */ }
    }
}

@Throws(SyncException::class)
fun `run`(`storageDir`: String, `dbFilePath`: String, `apiEndpoint`: String, `remoteToken`: String) =

    uniffiRustCallWithError(SyncException) { _status ->
        UniffiLib.INSTANCE.uniffi_cooklang_sync_client_fn_func_run(FfiConverterString.lower(`storageDir`), FfiConverterString.lower(`dbFilePath`), FfiConverterString.lower(`apiEndpoint`), FfiConverterString.lower(`remoteToken`), _status)
    }
