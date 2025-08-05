# State Design Pattern Implementation for Bluetooth Scan Flow

## Overview

This implementation demonstrates how the **State Design Pattern** can be used to manage the complex Bluetooth scan flow in your Android application. The pattern replaces complex conditional logic with a clean, maintainable state machine.

## Problem Solved

### Before (Original Implementation)
Your original `HomeViewModel.startScan()` method had complex conditional logic:

```kotlin
fun startScan() {
    if (homeStateValue.value.allRequiredReady) {
        bluetoothInteractor.startScnBluetooth()
    } else {
        with(homeState.value) {
            if (permissionState != true || bluetoothState != true)
                return
        }
        checkLocationStatus()
    }
}
```

**Issues:**
- Complex nested conditions
- Hard to extend with new prerequisites  
- Difficult to test individual scenarios
- State transitions are implicit
- Dialog management scattered throughout the code

### After (State Pattern Implementation)
```kotlin
fun startScan() {
    printLog("startScan() - delegating to state machine (current state: ${scanFlowContext.getCurrentStateName()})")
    scanFlowContext.startScan()
}
```

**Benefits:**
- Single line of code - all complexity handled by state machine
- Easy to extend with new states
- Each state is independently testable
- Clear state transitions with logging
- Centralized dialog management

## Architecture

### Core Components

1. **`ScanFlowState`** - Interface defining state behavior
2. **Concrete States** - Individual state implementations
3. **`ScanFlowContext`** - Context managing state transitions
4. **`HomeViewModelWithState`** - Refactored ViewModel using the pattern

### State Machine Flow

```
Idle → PermissionChecking → BluetoothChecking → LocationChecking → Scanning
  ↑           ↓                    ↓                   ↓             ↓
  ←─ PermissionDenied    BluetoothDisabled    LocationDisabled ──────┘
```

## States Explained

### 1. **IdleState**
- Initial state when nothing is active
- `startScan()` → requests permissions → transitions to `PermissionCheckingState`

### 2. **PermissionCheckingState** 
- Waiting for permission request result
- Permission granted → checks Bluetooth → `BluetoothCheckingState`
- Permission denied → shows dialog → `PermissionDeniedState`

### 3. **PermissionDeniedState**
- User denied permissions
- `startScan()` → shows permission alert dialog
- Permission granted → `BluetoothCheckingState`

### 4. **BluetoothCheckingState**
- Permissions granted, checking Bluetooth status
- Bluetooth enabled → checks location → `LocationCheckingState`
- Bluetooth disabled → shows dialog → `BluetoothDisabledState`

### 5. **BluetoothDisabledState**
- Bluetooth is turned off
- `startScan()` → shows Bluetooth dialog
- Bluetooth enabled → `LocationCheckingState`

### 6. **LocationCheckingState**
- Bluetooth enabled, checking location services
- Location enabled → starts scanning → `ScanningState`
- Location disabled → shows dialog → `LocationDisabledState`

### 7. **LocationDisabledState**
- Location services are off
- `startScan()` → shows location dialog
- Location enabled → `ScanningState`

### 8. **ScanningState**
- All prerequisites met, actively scanning
- Any prerequisite lost → stops scanning → appropriate error state
- `stopScan()` → `IdleState`

## Key Benefits

### 1. **Simplified Logic**
```kotlin
// Before: Complex nested conditions
if (homeStateValue.value.allRequiredReady) {
    bluetoothInteractor.startScnBluetooth()
} else {
    // Complex conditional logic...
}

// After: Single delegation
scanFlowContext.startScan()
```

### 2. **Easy to Extend**
Adding a new requirement (e.g., camera permission) is simple:
```kotlin
object CameraCheckingState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        // Handle camera permission logic
    }
    // ... other methods
}
```

### 3. **Individual State Testing**
Each state can be tested independently:
```kotlin
@Test
fun `Bluetooth disabled should show dialog and transition to BluetoothDisabled`() {
    context.onBluetoothStateChange(enabled = false)
    assertEquals("BluetoothDisabled", context.getCurrentStateName())
    assertEquals("BluetoothDisabled", dialogShown)
}
```

### 4. **Clear State Transitions**
All transitions are logged:
```
ScanFlow: Idle -> PermissionChecking
ScanFlow: PermissionChecking -> BluetoothChecking  
ScanFlow: BluetoothChecking -> LocationChecking
ScanFlow: LocationChecking -> Scanning
```

### 5. **Centralized Dialog Management**
```kotlin
interface DialogController {
    fun showPermissionAlertDialog()
    fun showPermissionDeniedDialog()
    fun showBluetoothDisabledDialog()
    fun showLocationDisabledDialog()
}
```

## Usage

### Integration Steps

1. **Replace your current ViewModel:**
```kotlin
// Change from HomeViewModel to HomeViewModelWithState
class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModelWithState by viewModels()
    // ... rest of code remains the same
}
```

2. **All existing UI code works unchanged** - the refactored ViewModel maintains the same interface

3. **Optional: Add state debugging:**
```kotlin
// Get current state for debugging
val currentState = viewModel.getCurrentScanState()
val isScanning = viewModel.isScanning()
```

### Testing

Run the included unit tests to see the state machine in action:

```bash
./gradlew test --tests ScanFlowStateTest
```

## Comparison: Before vs After

| Aspect | Before | After |
|--------|--------|-------|
| **Lines of Code** | `startScan()`: 20+ lines | `startScan()`: 2 lines |
| **Complexity** | High - nested conditions | Low - single delegation |
| **Testability** | Hard - need to mock entire flow | Easy - test individual states |
| **Extensibility** | Hard - modify existing logic | Easy - add new states |
| **Debugging** | Unclear state transitions | Clear state logging |
| **Maintainability** | Complex interdependencies | Clean separation of concerns |

## When to Use State Pattern

The State pattern is ideal when you have:

1. **Complex conditional logic** based on object state
2. **Multiple state-dependent behaviors** 
3. **State transitions** that need to be explicit
4. **Growing complexity** that will benefit from separation
5. **Need for individual state testing**

## Other Potential Applications in Your Codebase

Based on your Bluetooth tracker app, you could also apply the State pattern to:

1. **Connection State Management**
   - Disconnected → Connecting → Connected → Reconnecting
   
2. **Device Discovery Flow**
   - Idle → Discovering → DeviceFound → Connecting → Connected

3. **Data Sync State**
   - Idle → Syncing → Success → Error → Retry

## Conclusion

The State design pattern transforms your complex scan flow from a monolithic conditional structure into a clean, maintainable, and extensible state machine. Each state has a single responsibility, making the code easier to understand, test, and modify.

The pattern particularly shines in Android development where you often need to manage complex flows involving permissions, system services, and user interactions.